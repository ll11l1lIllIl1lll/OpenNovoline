/*
 * Copyright (c) 2016 Matsv
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package viaversion.viabackwards.protocol.protocol1_12_1to1_12_2;

import viaversion.viabackwards.api.BackwardsProtocol;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;

public class Protocol1_12_1To1_12_2 extends BackwardsProtocol<ClientboundPackets1_12_1, ClientboundPackets1_12_1, ServerboundPackets1_12_1, ServerboundPackets1_12_1> {

    public Protocol1_12_1To1_12_2() {
        super(ClientboundPackets1_12_1.class, ClientboundPackets1_12_1.class, ServerboundPackets1_12_1.class, ServerboundPackets1_12_1.class);
    }

    @Override
    protected void registerPackets() {
        registerOutgoing(ClientboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        Long keepAlive = packetWrapper.read(Type.LONG);
                        packetWrapper.user().get(KeepAliveTracker.class).setKeepAlive(keepAlive);
                        packetWrapper.write(Type.VAR_INT, keepAlive.hashCode());
                    }
                });
            }
        });

        registerIncoming(ServerboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int keepAlive = packetWrapper.read(Type.VAR_INT);
                        long realKeepAlive = packetWrapper.user().get(KeepAliveTracker.class).getKeepAlive();
                        if (keepAlive != Long.hashCode(realKeepAlive)) {
                            packetWrapper.cancel(); // Wrong data, cancel packet
                            return;
                        }
                        packetWrapper.write(Type.LONG, realKeepAlive);
                        // Reset KeepAliveTracker (to prevent sending same valid value in a row causing a timeout)
                        packetWrapper.user().get(KeepAliveTracker.class).setKeepAlive(Integer.MAX_VALUE);
                    }
                });
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new KeepAliveTracker(userConnection));
    }
}
