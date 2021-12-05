package viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets;

import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.rewriters.ItemRewriter;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import viaversion.viaversion.protocols.protocol1_16to1_15_2.data.RecipeRewriter1_16;

public class InventoryPackets {

    public static void register(Protocol1_16_2To1_16_1 protocol) {
        ItemRewriter itemRewriter = new ItemRewriter(protocol, InventoryPackets::toClient, InventoryPackets::toServer);

        itemRewriter.registerSetCooldown(ClientboundPackets1_16.COOLDOWN);
        itemRewriter.registerWindowItems(ClientboundPackets1_16.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
        itemRewriter.registerTradeList(ClientboundPackets1_16.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
        itemRewriter.registerSetSlot(ClientboundPackets1_16.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
        itemRewriter.registerEntityEquipmentArray(ClientboundPackets1_16.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
        itemRewriter.registerAdvancements(ClientboundPackets1_16.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);

        protocol.registerOutgoing(ClientboundPackets1_16.UNLOCK_RECIPES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT);
                    wrapper.passthrough(Type.BOOLEAN); // Open
                    wrapper.passthrough(Type.BOOLEAN); // Filter
                    wrapper.passthrough(Type.BOOLEAN); // Furnace
                    wrapper.passthrough(Type.BOOLEAN); // Filter furnace
                    // Blast furnace / smoker
                    wrapper.write(Type.BOOLEAN, false);
                    wrapper.write(Type.BOOLEAN, false);
                    wrapper.write(Type.BOOLEAN, false);
                    wrapper.write(Type.BOOLEAN, false);
                });
            }
        });

        new RecipeRewriter1_16(protocol, InventoryPackets::toClient).registerDefaultHandler(ClientboundPackets1_16.DECLARE_RECIPES);

        itemRewriter.registerClickWindow(ServerboundPackets1_16_2.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
        itemRewriter.registerCreativeInvAction(ServerboundPackets1_16_2.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
        protocol.registerIncoming(ServerboundPackets1_16_2.EDIT_BOOK, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> InventoryPackets.toServer(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
            }
        });

        itemRewriter.registerSpawnParticle(ClientboundPackets1_16.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.DOUBLE);
    }

    public static void toClient(Item item) {
        if (item == null) return;
        item.setIdentifier(Protocol1_16_2To1_16_1.MAPPINGS.getNewItemId(item.getIdentifier()));
    }

    public static void toServer(Item item) {
        if (item == null) return;
        item.setIdentifier(Protocol1_16_2To1_16_1.MAPPINGS.getOldItemId(item.getIdentifier()));
    }
}
