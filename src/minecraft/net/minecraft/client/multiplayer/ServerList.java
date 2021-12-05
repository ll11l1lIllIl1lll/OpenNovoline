package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public class ServerList {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The Minecraft instance.
     */
    private final Minecraft mc;
    private final List<ServerData> servers = Lists.newArrayList();

    public ServerList(Minecraft mcIn) {
        this.mc = mcIn;
        this.loadServerList();
    }

    /**
     * Loads a list of servers from servers.dat, by running ServerData.getServerDataFromNBTCompound on each NBT compound
     * found in the "servers" tag list.
     */
    public void loadServerList() {
        try {
            this.servers.clear();
            final NBTTagCompound tagCompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));

            if (tagCompound == null) {
                return;
            }

            final NBTTagList nbttaglist = tagCompound.getTagList("servers", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i)));
            }
        } catch (Exception exception) {
            LOGGER.error("Couldn't load server list", exception);
        }
    }

    /**
     * Runs getNBTCompound on each ServerData instance, puts everything into a "servers" NBT list and writes it to
     * servers.dat.
     */
    public void saveServerList() {
        try {
            final NBTTagList tagList = new NBTTagList();

            for (ServerData serverdata : this.servers) {
                tagList.appendTag(serverdata.getNBTCompound());
            }

            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("servers", tagList);
            CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.mcDataDir, "servers.dat"));
        } catch (Exception exception) {
            LOGGER.error("Couldn't save server list", exception);
        }
    }

    /**
     * Gets the ServerData instance stored for the given index in the list.
     */
    public ServerData getServerData(int p_78850_1_) {
        return this.servers.get(p_78850_1_);
    }

    /**
     * Removes the ServerData instance stored for the given index in the list.
     */
    public void removeServerData(int p_78851_1_) {
        this.servers.remove(p_78851_1_);
    }

    /**
     * Adds the given ServerData instance to the list.
     */
    public void addServerData(ServerData p_78849_1_) {
        this.servers.add(p_78849_1_);
    }

    /**
     * Counts the number of ServerData instances in the list.
     */
    public int countServers() {
        return this.servers.size();
    }

    /**
     * Takes two list indexes, and swaps their order around.
     */
    public void swapServers(int p_78857_1_, int p_78857_2_) {
        final ServerData serverdata = this.getServerData(p_78857_1_);
        this.servers.set(p_78857_1_, this.getServerData(p_78857_2_));
        this.servers.set(p_78857_2_, serverdata);
        this.saveServerList();
    }

    public void func_147413_a(int p_147413_1_, ServerData p_147413_2_) {
        this.servers.set(p_147413_1_, p_147413_2_);
    }

    public static void func_147414_b(ServerData p_147414_0_) {
        final ServerList serverlist = new ServerList(Minecraft.getInstance());
        serverlist.loadServerList();

        for (int i = 0; i < serverlist.countServers(); ++i) {
            final ServerData serverdata = serverlist.getServerData(i);

            if (serverdata.serverName.equals(p_147414_0_.serverName) && serverdata.serverIP.equals(p_147414_0_.serverIP)) {
                serverlist.func_147413_a(i, p_147414_0_);
                break;
            }
        }

        serverlist.saveServerList();
    }

}
