package viaversion.viaversion.protocols.protocol1_11to1_10.metadata;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.Entity1_11Types;
import viaversion.viaversion.api.entities.Entity1_11Types.EntityType;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import viaversion.viaversion.api.rewriters.MetadataRewriter;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import viaversion.viaversion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import viaversion.viaversion.protocols.protocol1_11to1_10.storage.EntityTracker1_11;

import java.util.List;
import java.util.Optional;

public class MetadataRewriter1_11To1_10 extends MetadataRewriter {

    public MetadataRewriter1_11To1_10(Protocol1_11To1_10 protocol) {
        super(protocol, EntityTracker1_11.class);
    }

    @Override
    protected void handleMetadata(int entityId, viaversion.viaversion.api.entities.EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
        if (metadata.getValue() instanceof Item) {
            // Apply rewrite
            EntityIdRewriter.toClientItem((Item) metadata.getValue());
        }

        if (type == null) return;
        if (type.is(EntityType.ELDER_GUARDIAN) || type.is(EntityType.GUARDIAN)) { // Guardians
            int oldid = metadata.getId();
            if (oldid == 12) {
                metadata.setMetaType(MetaType1_9.Boolean);
                boolean val = (((byte) metadata.getValue()) & 0x02) == 0x02;
                metadata.setValue(val);
            }
        }

        if (type.isOrHasParent(EntityType.ABSTRACT_SKELETON)) { // Skeletons
            int oldid = metadata.getId();
            if (oldid == 12) {
                metadatas.remove(metadata);
            }
            if (oldid == 13) {
                metadata.setId(12);
            }
        }

        if (type.isOrHasParent(EntityType.ZOMBIE)) { // Zombie | Zombie Villager | Husk
            if (type.is(EntityType.ZOMBIE, EntityType.HUSK) && metadata.getId() == 14) {
                metadatas.remove(metadata);
            } else {
                if (metadata.getId() == 15) {
                    metadata.setId(14);
                } else {
                    if (metadata.getId() == 14) {
                        metadata.setId(15);
                    }
                }
            }
        }

        if (type.isOrHasParent(EntityType.ABSTRACT_HORSE)) { // Horses
            // Remap metadata id
            int oldid = metadata.getId();
            if (oldid == 14) { // Type
                metadatas.remove(metadata);
            }
            if (oldid == 16) { // Owner
                metadata.setId(14);
            }
            if (oldid == 17) { // Armor
                metadata.setId(16);
            }

            // Process per type
            if (type.is(EntityType.HORSE)) {
                // Normal Horse
            } else {
                // Remove 15, 16
                if (metadata.getId() == 15 || metadata.getId() == 16) {
                    metadatas.remove(metadata);
                }
            }
            if (type.is(EntityType.DONKEY, EntityType.MULE)) {
                // Chested Horse
                if (metadata.getId() == 13) {
                    if ((((byte) metadata.getValue()) & 0x08) == 0x08) {
                        metadatas.add(new Metadata(15, MetaType1_9.Boolean, true));
                    } else {
                        metadatas.add(new Metadata(15, MetaType1_9.Boolean, false));
                    }
                }
            }
        }

        if (type.is(EntityType.ARMOR_STAND) && Via.getConfig().isHologramPatch()) {
            Metadata flags = getMetaByIndex(11, metadatas);
            Metadata customName = getMetaByIndex(2, metadatas);
            Metadata customNameVisible = getMetaByIndex(3, metadatas);
            if (metadata.getId() == 0 && flags != null && customName != null && customNameVisible != null) {
                byte data = (byte) metadata.getValue();
                // Check invisible | Check small | Check if custom name is empty | Check if custom name visible is true
                if ((data & 0x20) == 0x20 && ((byte) flags.getValue() & 0x01) == 0x01
                        && !((String) customName.getValue()).isEmpty() && (boolean) customNameVisible.getValue()) {
                    EntityTracker1_11 tracker = connection.get(EntityTracker1_11.class);
                    if (!tracker.isHologram(entityId)) {
                        tracker.addHologram(entityId);
                        try {
                            // Send movement
                            PacketWrapper wrapper = new PacketWrapper(0x25, null, connection);
                            wrapper.write(Type.VAR_INT, entityId);
                            wrapper.write(Type.SHORT, (short) 0);
                            wrapper.write(Type.SHORT, (short) (128D * (-Via.getConfig().getHologramYOffset() * 32D)));
                            wrapper.write(Type.SHORT, (short) 0);
                            wrapper.write(Type.BOOLEAN, true);

                            wrapper.send(Protocol1_11To1_10.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected viaversion.viaversion.api.entities.EntityType getTypeFromId(int type) {
        return Entity1_11Types.getTypeFromId(type, false);
    }

    @Override
    protected viaversion.viaversion.api.entities.EntityType getObjectTypeFromId(int type) {
        return Entity1_11Types.getTypeFromId(type, true);
    }

    public static EntityType rewriteEntityType(int numType, List<Metadata> metadata) {
        Optional<EntityType> optType = EntityType.findById(numType);
        if (!optType.isPresent()) {
            Via.getManager().getPlatform().getLogger().severe("Error: could not find Entity type " + numType + " with metadata: " + metadata);
            return null;
        }

        EntityType type = optType.get();

        try {
            if (type.is(EntityType.GUARDIAN)) {
                // ElderGuardian - 4
                Optional<Metadata> options = getById(metadata, 12);
                if (options.isPresent()) {
                    if ((((byte) options.get().getValue()) & 0x04) == 0x04) {
                        return EntityType.ELDER_GUARDIAN;
                    }
                }
            }
            if (type.is(EntityType.SKELETON)) {
                // WitherSkeleton - 5
                // Stray - 6
                Optional<Metadata> options = getById(metadata, 12);
                if (options.isPresent()) {
                    if (((int) options.get().getValue()) == 1) {
                        return EntityType.WITHER_SKELETON;
                    }
                    if (((int) options.get().getValue()) == 2) {
                        return EntityType.STRAY;
                    }
                }
            }
            if (type.is(EntityType.ZOMBIE)) {
                // ZombieVillager - 27
                // Husk - 23
                Optional<Metadata> options = getById(metadata, 13);
                if (options.isPresent()) {
                    int value = (int) options.get().getValue();
                    if (value > 0 && value < 6) {
                        metadata.add(new Metadata(16, MetaType1_9.VarInt, value - 1)); // Add profession type to new metadata
                        return EntityType.ZOMBIE_VILLAGER;
                    }
                    if (value == 6) {
                        return EntityType.HUSK;
                    }
                }
            }
            if (type.is(EntityType.HORSE)) {
                // SkeletonHorse - 28
                // ZombieHorse - 29
                // Donkey - 31
                // Mule - 32
                Optional<Metadata> options = getById(metadata, 14);
                if (options.isPresent()) {
                    if (((int) options.get().getValue()) == 0) {
                        return EntityType.HORSE;
                    }
                    if (((int) options.get().getValue()) == 1) {
                        return EntityType.DONKEY;
                    }
                    if (((int) options.get().getValue()) == 2) {
                        return EntityType.MULE;
                    }
                    if (((int) options.get().getValue()) == 3) {
                        return EntityType.ZOMBIE_HORSE;
                    }
                    if (((int) options.get().getValue()) == 4) {
                        return EntityType.SKELETON_HORSE;
                    }
                }
            }
        } catch (Exception e) {
            if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().warning("An error occurred with entity type rewriter");
                Via.getPlatform().getLogger().warning("Metadata: " + metadata);
                e.printStackTrace();
            }
        }

        return type;
    }

    public static Optional<Metadata> getById(List<Metadata> metadatas, int id) {
        for (Metadata metadata : metadatas) {
            if (metadata.getId() == id) return Optional.of(metadata);
        }
        return Optional.empty();
    }
}
