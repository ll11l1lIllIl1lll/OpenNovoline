package viaversion.viaversion.api.rewriters;

import viaversion.viaversion.api.minecraft.BlockChangeRecord;
import viaversion.viaversion.api.minecraft.Position;
import viaversion.viaversion.api.protocol.ClientboundPacketType;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;

// If any of these methods become outdated, just create a new rewriter overriding the methods
public class BlockRewriter {
    private final Protocol protocol;
    private final Type<Position> positionType;

    public BlockRewriter(Protocol protocol, Type<Position> positionType) {
        this.protocol = protocol;
        this.positionType = positionType;
    }

    public void registerBlockAction(ClientboundPacketType packetType) {
        protocol.registerOutgoing(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(positionType); // Location
                map(Type.UNSIGNED_BYTE); // Action id
                map(Type.UNSIGNED_BYTE); // Action param
                map(Type.VAR_INT); // Block id - /!\ NOT BLOCK STATE
                handler(wrapper -> {
                    int id = wrapper.get(Type.VAR_INT, 0);
                    int mappedId = protocol.getMappingData().getNewBlockId(id);
                    if (mappedId == -1) {
                        // Block (action) has been removed
                        wrapper.cancel();
                        return;
                    }

                    wrapper.set(Type.VAR_INT, 0, mappedId);
                });
            }
        });
    }

    public void registerBlockChange(ClientboundPacketType packetType) {
        protocol.registerOutgoing(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(positionType);
                map(Type.VAR_INT);
                handler(wrapper -> wrapper.set(Type.VAR_INT, 0, protocol.getMappingData().getNewBlockStateId(wrapper.get(Type.VAR_INT, 0))));
            }
        });
    }

    public void registerMultiBlockChange(ClientboundPacketType packetType) {
        protocol.registerOutgoing(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // 0 - Chunk X
                map(Type.INT); // 1 - Chunk Z
                handler(wrapper -> {
                    for (BlockChangeRecord record : wrapper.passthrough(Type.BLOCK_CHANGE_RECORD_ARRAY)) {
                        record.setBlockId(protocol.getMappingData().getNewBlockStateId(record.getBlockId()));
                    }
                });
            }
        });
    }

    public void registerVarLongMultiBlockChange(ClientboundPacketType packetType) {
        protocol.registerOutgoing(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.LONG); // Chunk position
                handler(wrapper -> {
                    for (BlockChangeRecord record : wrapper.passthrough(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY)) {
                        record.setBlockId(protocol.getMappingData().getNewBlockStateId(record.getBlockId()));
                    }
                });
            }
        });
    }

    public void registerAcknowledgePlayerDigging(ClientboundPacketType packetType) {
        // Same exact handler
        registerBlockChange(packetType);
    }

    public void registerEffect(ClientboundPacketType packetType, int playRecordId, int blockBreakId) {
        protocol.registerOutgoing(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Effect Id
                map(positionType); // Location
                map(Type.INT); // Data
                handler(wrapper -> {
                    int id = wrapper.get(Type.INT, 0);
                    int data = wrapper.get(Type.INT, 1);
                    if (id == playRecordId) { // Play record
                        wrapper.set(Type.INT, 1, protocol.getMappingData().getNewItemId(data));
                    } else if (id == blockBreakId) { // Block break + block break sound
                        wrapper.set(Type.INT, 1, protocol.getMappingData().getNewBlockStateId(data));
                    }
                });
            }
        });
    }
}
