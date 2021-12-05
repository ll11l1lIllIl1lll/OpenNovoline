package viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import viaversion.viabackwards.ViaBackwards;
import viaversion.viabackwards.api.rewriters.Rewriter;
import viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.NamedSoundMapping;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;

public class SoundPackets1_13 extends Rewriter<Protocol1_12_2To1_13> {
    private static final String[] SOUND_SOURCES = {"master", "music", "record", "weather", "block", "hostile", "neutral", "player", "ambient", "voice"};

    public SoundPackets1_13(Protocol1_12_2To1_13 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        protocol.registerOutgoing(ClientboundPackets1_13.NAMED_SOUND, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(wrapper -> {
                    String newSound = wrapper.get(Type.STRING, 0);
                    if (newSound.startsWith("minecraft:")) {
                        newSound = newSound.substring(10);
                    }

                    String oldSound = NamedSoundMapping.getOldId(newSound);
                    if (oldSound != null || (oldSound = protocol.getMappingData().getMappedNamedSound(newSound)) != null) {
                        wrapper.set(Type.STRING, 0, oldSound);
                    } else if (!Via.getConfig().isSuppressConversionWarnings()) {
                        ViaBackwards.getPlatform().getLogger().warning("Unknown named sound in 1.13->1.12 protocol: " + newSound);
                    }
                });
            }
        });

        // Stop Sound -> Plugin Message
        protocol.registerOutgoing(ClientboundPackets1_13.STOP_SOUND, ClientboundPackets1_12_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.write(Type.STRING, "MC|StopSound");
                    byte flags = wrapper.read(Type.BYTE);
                    String source;
                    if ((flags & 0x01) != 0) {
                        source = SOUND_SOURCES[wrapper.read(Type.VAR_INT)];
                    } else {
                        source = "";
                    }

                    String sound;
                    if ((flags & 0x02) != 0) {
                        String newSound = wrapper.read(Type.STRING);
                        if (newSound.startsWith("minecraft:")) {
                            newSound = newSound.substring(10);
                        }

                        sound = protocol.getMappingData().getMappedNamedSound(newSound);
                        if (sound == null) {
                            sound = "";
                        }
                    } else {
                        sound = "";
                    }

                    wrapper.write(Type.STRING, source);
                    wrapper.write(Type.STRING, sound);
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_13.SOUND, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                handler(wrapper -> {
                    int newSound = wrapper.get(Type.VAR_INT, 0);
                    int oldSound = protocol.getMappingData().getSoundMappings().getNewId(newSound);
                    if (oldSound == -1) {
                        wrapper.cancel();
                    } else {
                        wrapper.set(Type.VAR_INT, 0, oldSound);
                    }
                });
            }
        });
    }
}
