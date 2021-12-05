package viaversion.viabackwards.api.rewriters;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import viaversion.viabackwards.ViaBackwards;
import viaversion.viabackwards.api.BackwardsProtocol;
import viaversion.viabackwards.api.entities.meta.MetaHandlerEvent;
import viaversion.viabackwards.api.entities.meta.MetaHandlerSettings;
import viaversion.viabackwards.api.entities.storage.EntityData;
import viaversion.viabackwards.api.entities.storage.EntityTracker;
import viaversion.viabackwards.api.entities.storage.MetaStorage;
import viaversion.viabackwards.api.exceptions.RemovedValueException;
import org.jetbrains.annotations.Nullable;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.Via;
import viaversion.viaversion.api.data.ParticleMappings;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.entities.EntityType;
import viaversion.viaversion.api.minecraft.metadata.MetaType;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.protocol.ClientboundPacketType;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.api.type.types.Particle;
import viaversion.viaversion.exception.CancelException;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Entity rewriter base class.
 *
 * @see EntityRewriter
 * @see LegacyEntityRewriter
 */
public abstract class EntityRewriterBase<T extends BackwardsProtocol> extends Rewriter<T> {
    private final Map<EntityType, EntityData> entityTypes = new HashMap<>();
    private final List<MetaHandlerSettings> metaHandlers = new ArrayList<>();
    private final MetaType displayNameMetaType;
    private final MetaType displayVisibilityMetaType;
    private final int displayNameIndex;
    private final int displayVisibilityIndex;
    protected Int2IntMap typeMapping;

    EntityRewriterBase(T protocol, MetaType displayNameMetaType, int displayNameIndex,
                       MetaType displayVisibilityMetaType, int displayVisibilityIndex) {
        super(protocol);
        this.displayNameMetaType = displayNameMetaType;
        this.displayNameIndex = displayNameIndex;
        this.displayVisibilityMetaType = displayVisibilityMetaType;
        this.displayVisibilityIndex = displayVisibilityIndex;
    }

    protected EntityType getEntityType(UserConnection connection, int id) {
        return getEntityTracker(connection).getEntityType(id);
    }

    protected void addTrackedEntity(PacketWrapper wrapper, int entityId, EntityType type) throws Exception {
        getEntityTracker(wrapper.user()).trackEntityType(entityId, type);
    }

    protected boolean hasData(EntityType type) {
        return entityTypes.containsKey(type);
    }

    @Nullable
    protected EntityData getEntityData(EntityType type) {
        return entityTypes.get(type);
    }

    /**
     * Note that both types should be of the SAME version.
     *
     * @param oldType     old type of the higher version
     * @param replacement new type of the higher version
     * @return created entity data
     * @see #mapEntityDirect(EntityType, EntityType) for id only rewriting
     */
    protected EntityData mapEntity(EntityType oldType, EntityType replacement) {
        Preconditions.checkArgument(oldType.getClass() == replacement.getClass());

        // Already rewrite the id here
        int mappedReplacementId = getOldEntityId(replacement.getId());
        EntityData data = new EntityData(oldType.getId(), mappedReplacementId);
        mapEntityDirect(oldType.getId(), mappedReplacementId);
        entityTypes.put(oldType, data);
        return data;
    }

    /**
     * Maps entity ids based on the enum constant's names.
     *
     * @param oldTypes     entity types of the higher version
     * @param newTypeClass entity types enum class of the lower version
     * @param <T>          new type class
     */
    public <T extends Enum<T> & EntityType> void mapTypes(EntityType[] oldTypes, Class<T> newTypeClass) {
        if (typeMapping == null) {
            typeMapping = new Int2IntOpenHashMap(oldTypes.length, 1F);
            typeMapping.defaultReturnValue(-1);
        }
        for (EntityType oldType : oldTypes) {
            try {
                T newType = Enum.valueOf(newTypeClass, oldType.name());
                typeMapping.put(oldType.getId(), newType.getId());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    /**
     * Directly maps the entity without any other rewriting.
     *
     * @param oldType type of the higher version
     * @param newType type of the lower version
     * @see #mapEntity(EntityType, EntityType) for mapping with data
     */
    public void mapEntityDirect(EntityType oldType, EntityType newType) {
        Preconditions.checkArgument(oldType.getClass() != newType.getClass());
        mapEntityDirect(oldType.getId(), newType.getId());
    }

    private void mapEntityDirect(int oldType, int newType) {
        if (typeMapping == null) {
            typeMapping = new Int2IntOpenHashMap();
            typeMapping.defaultReturnValue(-1);
        }
        typeMapping.put(oldType, newType);
    }

    public MetaHandlerSettings registerMetaHandler() {
        MetaHandlerSettings settings = new MetaHandlerSettings();
        metaHandlers.add(settings);
        return settings;
    }

    protected MetaStorage handleMeta(UserConnection user, int entityId, MetaStorage storage) throws Exception {
        EntityTracker.StoredEntity storedEntity = getEntityTracker(user).getEntity(entityId);
        if (storedEntity == null) {
            if (!Via.getConfig().isSuppressMetadataErrors()) {
                ViaBackwards.getPlatform().getLogger().warning("Metadata for entity id: " + entityId + " not sent because the entity doesn't exist. " + storage);
            }
            throw CancelException.CACHED;
        }

        EntityType type = storedEntity.getType();
        for (MetaHandlerSettings settings : metaHandlers) {
            List<Metadata> newData = new ArrayList<>();
            for (Metadata meta : storage.getMetaDataList()) {
                MetaHandlerEvent event = null;
                try {
                    Metadata modifiedMeta = meta;
                    if (settings.isGucci(type, meta)) {
                        event = new MetaHandlerEvent(user, storedEntity, meta.getId(), meta, storage);
                        modifiedMeta = settings.getHandler().handle(event);

                        if (event.getExtraData() != null) {
                            newData.addAll(event.getExtraData());
                            event.clearExtraData();
                        }
                    }

                    if (modifiedMeta == null) {
                        throw RemovedValueException.EX;
                    }

                    newData.add(modifiedMeta);
                } catch (RemovedValueException e) {
                    // Add the additionally created data here in case of an interruption
                    if (event != null && event.getExtraData() != null) {
                        newData.addAll(event.getExtraData());
                    }
                } catch (Exception e) {
                    Logger log = ViaBackwards.getPlatform().getLogger();
                    log.warning("Unable to handle metadata " + meta + " for entity type " + type);
                    log.warning(storage.getMetaDataList().stream().sorted(Comparator.comparingInt(Metadata::getId))
                            .map(Metadata::toString).collect(Collectors.joining("\n", "Full metadata list: ", "")));
                    e.printStackTrace();
                }
            }

            storage.setMetaDataList(newData);
        }

        // Handle Entity Name
        Metadata data = storage.get(displayNameIndex);
        if (data != null) {
            EntityData entityData = getEntityData(type);
            // Set the name if there is no custom name set already
            if (entityData != null && entityData.getMobName() != null
                    && (data.getValue() == null || data.getValue().toString().isEmpty())
                    && data.getMetaType().getTypeID() == displayNameMetaType.getTypeID()) {
                data.setValue(entityData.getMobName());
                if (ViaBackwards.getConfig().alwaysShowOriginalMobName()) {
                    storage.delete(displayVisibilityIndex);
                    storage.add(new Metadata(displayVisibilityIndex, displayVisibilityMetaType, true));
                }
            }
        }

        return storage;
    }

    /**
     * Helper method to handle player, painting, or xp orb trackers without meta changes.
     */
    protected void registerExtraTracker(ClientboundPacketType packetType, EntityType entityType, Type intType) {
        getProtocol().registerOutgoing(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(intType); // 0 - Entity id
                handler(wrapper -> addTrackedEntity(wrapper, (int) wrapper.get(intType, 0), entityType));
            }
        });
    }

    protected void registerExtraTracker(ClientboundPacketType packetType, EntityType entityType) {
        registerExtraTracker(packetType, entityType, Type.VAR_INT);
    }

    protected void registerEntityDestroy(ClientboundPacketType packetType) {
        getProtocol().registerOutgoing(packetType, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT_ARRAY_PRIMITIVE); // 0 - Entity ids
                handler(wrapper -> {
                    EntityTracker.ProtocolEntityTracker tracker = getEntityTracker(wrapper.user());
                    for (int entity : wrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0)) {
                        tracker.removeEntity(entity);
                    }
                });
            }
        });
    }

    // ONLY TRACKS, DOESN'T REWRITE IDS
    protected PacketHandler getTrackerHandler(Type intType, int typeIndex) {
        return wrapper -> {
            Number id = (Number) wrapper.get(intType, typeIndex);
            addTrackedEntity(wrapper, wrapper.get(Type.VAR_INT, 0), getTypeFromId(id.intValue()));
        };
    }

    protected PacketHandler getTrackerHandler() {
        return getTrackerHandler(Type.VAR_INT, 1);
    }

    protected PacketHandler getTrackerHandler(EntityType entityType, Type intType) {
        return wrapper -> addTrackedEntity(wrapper, (int) wrapper.get(intType, 0), entityType);
    }

    protected PacketHandler getDimensionHandler(int index) {
        return wrapper -> {
            ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
            int dimensionId = wrapper.get(Type.INT, index);
            clientWorld.setEnvironment(dimensionId);
        };
    }

    public EntityTracker.ProtocolEntityTracker getEntityTracker(UserConnection user) {
        return user.get(EntityTracker.class).get(getProtocol());
    }

    protected void rewriteParticle(Particle particle) {
        ParticleMappings mappings = protocol.getMappingData().getParticleMappings();
        int id = particle.getId();
        if (id == mappings.getBlockId() || id == mappings.getFallingDustId()) {
            Particle.ParticleData data = particle.getArguments().get(0);
            data.setValue(protocol.getMappingData().getNewBlockStateId(data.get()));
        } else if (id == mappings.getItemId()) {
            Particle.ParticleData data = particle.getArguments().get(0);
            data.setValue(protocol.getMappingData().getNewItemId(data.get()));
        }

        particle.setId(protocol.getMappingData().getNewParticleId(id));
    }

    protected abstract EntityType getTypeFromId(int typeId);

    public int getOldEntityId(int newId) {
        return typeMapping != null ? typeMapping.getOrDefault(newId, newId) : newId;
    }
}
