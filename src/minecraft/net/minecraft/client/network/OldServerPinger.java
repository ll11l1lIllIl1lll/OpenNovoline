package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OldServerPinger {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final ServerData server) throws UnknownHostException {
        final ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
        final NetworkManager networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        this.pingDestinations.add(networkmanager);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;
        networkmanager.setNetHandler(new INetHandlerStatusClient() {

            private boolean field_147403_d = false;
            private boolean field_183009_e = false;
            private long field_175092_e = 0L;

            public void handleServerInfo(S00PacketServerInfo packetIn) {
                if (this.field_183009_e) {
                    networkmanager.closeChannel(new ChatComponentText("Received unrequested status"));
                } else {
                    this.field_183009_e = true;
                    final ServerStatusResponse serverStatusResponse = packetIn.getResponse();

                    if (serverStatusResponse.getServerDescription() != null) {
                        server.serverMOTD = serverStatusResponse.getServerDescription().getFormattedText();
                    } else {
                        server.serverMOTD = "";
                    }

                    if (serverStatusResponse.getProtocolVersionInfo() != null) {
                        server.gameVersion = serverStatusResponse.getProtocolVersionInfo().getName();
                        server.version = serverStatusResponse.getProtocolVersionInfo().getProtocol();
                    } else {
                        server.gameVersion = "Old";
                        server.version = 0;
                    }

                    if (serverStatusResponse.getPlayerCountData() != null) {
                        server.populationInfo = EnumChatFormatting.GRAY + "" + serverStatusResponse.getPlayerCountData().getOnlinePlayerCount() + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + serverStatusResponse.getPlayerCountData().getMaxPlayers();

                        if (ArrayUtils.isNotEmpty(serverStatusResponse.getPlayerCountData().getPlayers())) {
                            final StringBuilder stringbuilder = new StringBuilder();

                            for (GameProfile gameprofile : serverStatusResponse.getPlayerCountData().getPlayers()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append(gameprofile.getName());
                            }

                            if (serverStatusResponse.getPlayerCountData().getPlayers().length < serverStatusResponse.getPlayerCountData().getOnlinePlayerCount()) {
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append("... and ").append(serverStatusResponse.getPlayerCountData().getOnlinePlayerCount() - serverStatusResponse.getPlayerCountData().getPlayers().length).append(" more ...");
                            }

                            server.playerList = stringbuilder.toString();
                        }
                    } else {
                        server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                    }

                    if (serverStatusResponse.getFavicon() != null) {
                        final String s = serverStatusResponse.getFavicon();

                        if (s.startsWith("data:image/png;base64,")) {
                            server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                        } else {
                            OldServerPinger.LOGGER.error("Invalid server icon (unknown format)");
                        }
                    } else {
                        server.setBase64EncodedIconData(null);
                    }

                    this.field_175092_e = Minecraft.getSystemTime();
                    networkmanager.sendPacket(new C01PacketPing(this.field_175092_e));
                    this.field_147403_d = true;
                }
            }

            public void handlePong(S01PacketPong packetIn) {
                final long i = this.field_175092_e;
                final long j = Minecraft.getSystemTime();
                server.pingToServer = j - i;
                networkmanager.closeChannel(new ChatComponentText("Finished"));
            }

            public void onDisconnect(IChatComponent reason) {
                if (!this.field_147403_d) {
                    OldServerPinger.LOGGER.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                    server.populationInfo = "";
                    OldServerPinger.this.tryCompatibilityPing(server);
                }
            }
        });

        try {
            networkmanager.sendPacket(new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
            networkmanager.sendPacket(new C00PacketServerQuery());
        } catch (Throwable throwable) {
            LOGGER.error(throwable);
        }
    }

    private void tryCompatibilityPing(final ServerData server) {
        final ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
        new Bootstrap().group(NetworkManager.CLIENT_NIO_EVENT_LOOP.getValue()).handler(new ChannelInitializer<Channel>() {

            protected void initChannel(Channel p_initChannel_1_) throws Exception {
                try {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
                } catch (ChannelException var3) {
                }

                p_initChannel_1_.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
                        super.channelActive(p_channelActive_1_);
                        final ByteBuf bytebuf = Unpooled.buffer();

                        try {
                            bytebuf.writeByte(254);
                            bytebuf.writeByte(1);
                            bytebuf.writeByte(250);
                            char[] achar = "MC|PingHost".toCharArray();
                            bytebuf.writeShort(achar.length);

                            for (char c0 : achar) {
                                bytebuf.writeChar(c0);
                            }

                            bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                            bytebuf.writeByte(127);
                            achar = serveraddress.getIP().toCharArray();
                            bytebuf.writeShort(achar.length);

                            for (char c1 : achar) {
                                bytebuf.writeChar(c1);
                            }

                            bytebuf.writeInt(serveraddress.getPort());
                            p_channelActive_1_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        } finally {
                            bytebuf.release();
                        }
                    }

                    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, ByteBuf p_channelRead0_2_) throws Exception {
                        final short short1 = p_channelRead0_2_.readUnsignedByte();

                        if (short1 == 255) {
                            final String s = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), Charsets.UTF_16BE);
                            final String[] strings = Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split(s), String.class);

                            if ("\u00a71".equals(strings[0])) {
                                final int i = MathHelper.parseIntWithDefault(strings[1], 0);
                                final String s1 = strings[2];
                                final String s2 = strings[3];
                                final int j = MathHelper.parseIntWithDefault(strings[4], -1);
                                final int k = MathHelper.parseIntWithDefault(strings[5], -1);
                                server.version = -1;
                                server.gameVersion = s1;
                                server.serverMOTD = s2;
                                server.populationInfo = EnumChatFormatting.GRAY + "" + j + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + k;
                            }
                        }

                        p_channelRead0_1_.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception {
                        p_exceptionCaught_1_.close();
                    }
                });
            }
        }).channel(NioSocketChannel.class).connect(serveraddress.getIP(), serveraddress.getPort());
    }

    public void pingPendingNetworks() {
        synchronized (this.pingDestinations) {
            final Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen()) {
                    networkmanager.processReceivedPackets();
                } else {
                    iterator.remove();
                    networkmanager.checkDisconnected();
                }
            }
        }
    }

    public void clearPendingNetworks() {
        synchronized (this.pingDestinations) {
            final Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen()) {
                    iterator.remove();
                    networkmanager.closeChannel(new ChatComponentText("Cancelled"));
                }
            }
        }
    }

}
