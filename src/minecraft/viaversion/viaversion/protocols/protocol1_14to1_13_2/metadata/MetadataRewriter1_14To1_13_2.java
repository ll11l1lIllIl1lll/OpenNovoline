package viaversion.viaversion.protocols.protocol1_14to1_13_2.metadata;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_13Types;
import viaversion.viaversion.api.entities.Entity1_14Types;
import viaversion.viaversion.api.entities.EntityType;
import viaversion.viaversion.api.minecraft.VillagerData;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import viaversion.viaversion.api.rewriters.MetadataRewriter;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.Particle;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;

import java.util.List;

public class MetadataRewriter1_14To1_13_2 extends MetadataRewriter {

    public MetadataRewriter1_14To1_13_2(Protocol1_14To1_13_2 protocol) {
        super(protocol, EntityTracker1_14.class);
        mapTypes(Entity1_13Types.EntityType.values(), Entity1_14Types.EntityType.class);
        mapType(Entity1_13Types.EntityType.OCELOT, Entity1_14Types.EntityType.CAT); //TODO remap untamed ocelots to ocelots?
    }

    @Override
    protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
        metadata.setMetaType(MetaType1_14.byId(metadata.getMetaType().getTypeID()));

        EntityTracker1_14 tracker = connection.get(EntityTracker1_14.class);

        if (metadata.getMetaType() == MetaType1_14.Slot) {
            InventoryPackets.toClient((Item) metadata.getValue());
        } else if (metadata.getMetaType() == MetaType1_14.BlockID) {
            // Convert to new block id
            int data = (int) metadata.getValue();
            metadata.setValue(protocol.getMappingData().getNewBlockStateId(data));
        }

        if (type == null) return;

        //Metadata 6 added to abstract_entity
        if (metadata.getId() > 5) {
            metadata.setId(metadata.getId() + 1);
        }
        if (metadata.getId() == 8 && type.isOrHasParent(Entity1_14Types.EntityType.LIVINGENTITY)) {
            final float v = ((Number) metadata.getValue()).floatValue();
            if (Float.isNaN(v) && Via.getConfig().is1_14HealthNaNFix()) {
                metadata.setValue(1F);
            }
        }

        //Metadata 12 added to living_entity
        if (metadata.getId() > 11 && type.isOrHasParent(Entity1_14Types.EntityType.LIVINGENTITY)) {
            metadata.setId(metadata.getId() + 1);
        }

        if (type.isOrHasParent(Entity1_14Types.EntityType.ABSTRACT_INSENTIENT)) {
            if (metadata.getId() == 13) {
                tracker.setInsentientData(entityId, (byte) ((((Number) metadata.getValue()).byteValue() & ~0x4)
                        | (tracker.getInsentientData(entityId) & 0x4))); // New attacking metadata
                metadata.setValue(tracker.getInsentientData(entityId));
            }
        }

        if (type.isOrHasParent(Entity1_14Types.EntityType.PLAYER)) {
            if (entityId != tracker.getClientEntityId()) {
                if (metadata.getId() == 0) {
                    byte flags = ((Number) metadata.getValue()).byteValue();
                    // Mojang overrides the client-side pose updater, see OtherPlayerEntity#updateSize
                    tracker.setEntityFlags(entityId, flags);
                } else if (metadata.getId() == 7) {
                    tracker.setRiptide(entityId, (((Number) metadata.getValue()).byteValue() & 0x4) != 0);
                }
                if (metadata.getId() == 0 || metadata.getId() == 7) {
                    metadatas.add(new Metadata(6, MetaType1_14.Pose, recalculatePlayerPose(entityId, tracker)));
                }
            }
        } else if (type.isOrHasParent(Entity1_14Types.EntityType.ZOMBIE)) {
            if (metadata.getId() == 16) {
                tracker.setInsentientData(entityId, (byte) ((tracker.getInsentientData(entityId) & ~0x4)
                        | ((boolean) metadata.getValue() ? 0x4 : 0))); // New attacking
                metadatas.remove(metadata);  // "Are hands held up"
                metadatas.add(new Metadata(13, MetaType1_14.Byte, tracker.getInsentientData(entityId)));
            } else if (metadata.getId() > 16) {
                metadata.setId(metadata.getId() - 1);
            }
        }

        if (type.isOrHasParent(Entity1_14Types.EntityType.MINECART_ABSTRACT)) {
            if (metadata.getId() == 10) {
                // New block format
                int data = (int) metadata.getValue();
                metadata.setValue(protocol.getMappingData().getNewBlockStateId(data));
            }
        } else if (type.is(Entity1_14Types.EntityType.HORSE)) {
            if (metadata.getId() == 18) {
                metadatas.remove(metadata);

                int armorType = (int) metadata.getValue();
                Item armorItem = null;
                if (armorType == 1) {  //iron armor
                    armorItem = new Item(protocol.getMappingData().getNewItemId(727), (byte) 1, (short) 0, null);
                } else if (armorType == 2) {  //gold armor
                    armorItem = new Item(protocol.getMappingData().getNewItemId(728), (byte) 1, (short) 0, null);
                } else if (armorType == 3) {  //diamond armor
                    armorItem = new Item(protocol.getMappingData().getNewItemId(729), (byte) 1, (short) 0, null);
                }

                PacketWrapper equipmentPacket = new PacketWrapper(0x46, null, connection);
                equipmentPacket.write(Type.VAR_INT, entityId);
                equipmentPacket.write(Type.VAR_INT, 4);
                equipmentPacket.write(Type.FLAT_VAR_INT_ITEM, armorItem);
                equipmentPacket.send(Protocol1_14To1_13_2.class);
            }
        } else if (type.is(Entity1_14Types.EntityType.VILLAGER)) {
            if (metadata.getId() == 15) {
                // plains
                metadata.setValue(new VillagerData(2, getNewProfessionId((int) metadata.getValue()), 0));
                metadata.setMetaType(MetaType1_14.VillagerData);
            }
        } else if (type.is(Entity1_14Types.EntityType.ZOMBIE_VILLAGER)) {
            if (metadata.getId() == 18) {
                // plains
                metadata.setValue(new VillagerData(2, getNewProfessionId((int) metadata.getValue()), 0));
                metadata.setMetaType(MetaType1_14.VillagerData);
            }
        } else if (type.isOrHasParent(Entity1_14Types.EntityType.ABSTRACT_ARROW)) {
            if (metadata.getId() >= 9) { // New piercing
                metadata.setId(metadata.getId() + 1);
            }
        } else if (type.is(Entity1_14Types.EntityType.FIREWORK_ROCKET)) {
            if (metadata.getId() == 8) {
                if (metadata.getValue().equals(0))
                    metadata.setValue(null); // https://bugs.mojang.com/browse/MC-111480
                metadata.setMetaType(MetaType1_14.OptVarInt);
            }
        } else if (type.isOrHasParent(Entity1_14Types.EntityType.ABSTRACT_SKELETON)) {
            if (metadata.getId() == 14) {
                tracker.setInsentientData(entityId, (byte) ((tracker.getInsentientData(entityId) & ~0x4)
                        | ((boolean) metadata.getValue() ? 0x4 : 0))); // New attacking
                metadatas.remove(metadata);  // "Is swinging arms"
                metadatas.add(new Metadata(13, MetaType1_14.Byte, tracker.getInsentientData(entityId)));
            }
        } else if (type.is(Entity1_14Types.EntityType.AREA_EFFECT_CLOUD)) {
            if (metadata.getId() == 10) {
                rewriteParticle((Particle) metadata.getValue());
            }
        }

        if (type.isOrHasParent(Entity1_14Types.EntityType.ABSTRACT_ILLAGER_BASE)) {
            if (metadata.getId() == 14) {
                tracker.setInsentientData(entityId, (byte) ((tracker.getInsentientData(entityId) & ~0x4)
                        | (((Number) metadata.getValue()).byteValue() != 0 ? 0x4 : 0))); // New attacking
                metadatas.remove(metadata);  // "Has target (aggressive state)"
                metadatas.add(new Metadata(13, MetaType1_14.Byte, tracker.getInsentientData(entityId)));
            }
        }

        // TODO Are witch and ravager also abstract illagers? They all inherit the new metadata 14 added in 19w13a
        if (type.is(Entity1_14Types.EntityType.WITCH) || type.is(Entity1_14Types.EntityType.RAVAGER) || type.isOrHasParent(Entity1_14Types.EntityType.ABSTRACT_ILLAGER_BASE)) {
            if (metadata.getId() >= 14) {  // TODO 19w13 added a new boolean (raid participant - is celebrating) with id 14
                metadata.setId(metadata.getId() + 1);
            }
        }
    }

    @Override
    protected EntityType getTypeFromId(int type) {
        return Entity1_14Types.getTypeFromId(type);
    }

    private static boolean isSneaking(byte flags) {
        return (flags & 0x2) != 0;
    }

    private static boolean isSwimming(byte flags) {
        return (flags & 0x10) != 0;
    }

    private static int getNewProfessionId(int old) {
        // profession -> career
        switch (old) {
            case 0: // farmer
                return 5;
            case 1: // librarian
                return 9;
            case 2: // priest
                return 4; // cleric
            case 3: // blacksmith
                return 1; // armorer
            case 4: // butcher
                return 2;
            case 5: // nitwit
                return 11;
            default:
                return 0; // none
        }
    }

    private static boolean isFallFlying(int entityFlags) {
        return (entityFlags & 0x80) != 0;
    }

    public static int recalculatePlayerPose(int entityId, EntityTracker1_14 tracker) {
        byte flags = tracker.getEntityFlags(entityId);
        // Mojang overrides the client-side pose updater, see OtherPlayerEntity#updateSize
        int pose = 0; // standing
        if (isFallFlying(flags)) {
            pose = 1;
        } else if (tracker.isSleeping(entityId)) {
            pose = 2;
        } else if (isSwimming(flags)) {
            pose = 3;
        } else if (tracker.isRiptide(entityId)) {
            pose = 4;
        } else if (isSneaking(flags)) {
            pose = 5;
        }
        return pose;
    }
}
