package viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.PacketHandler;
import viaversion.viaversion.api.remapper.PacketRemapper;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;

public class InventoryPackets {

    public static void register(Protocol protocol) {
        protocol.registerOutgoing(ClientboundPackets1_13.SET_SLOT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // 0 - Window ID
                map(Type.SHORT); // 1 - Slot ID
                map(Type.FLAT_ITEM, Type.FLAT_VAR_INT_ITEM); // 2 - Slot Value
            }
        });
        protocol.registerOutgoing(ClientboundPackets1_13.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // 0 - Window ID
                map(Type.FLAT_ITEM_ARRAY, Type.FLAT_VAR_INT_ITEM_ARRAY); // 1 - Window Values
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Channel
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String channel = wrapper.get(Type.STRING, 0);
                        if (channel.equals("minecraft:trader_list") || channel.equals("trader_list")) {
                            wrapper.passthrough(Type.INT); // Passthrough Window ID

                            int size = wrapper.passthrough(Type.UNSIGNED_BYTE);
                            for (int i = 0; i < size; i++) {
                                // Input Item
                                wrapper.write(Type.FLAT_VAR_INT_ITEM, wrapper.read(Type.FLAT_ITEM));
                                // Output Item
                                wrapper.write(Type.FLAT_VAR_INT_ITEM, wrapper.read(Type.FLAT_ITEM));

                                boolean secondItem = wrapper.passthrough(Type.BOOLEAN); // Has second item
                                if (secondItem) {
                                    wrapper.write(Type.FLAT_VAR_INT_ITEM, wrapper.read(Type.FLAT_ITEM));
                                }

                                wrapper.passthrough(Type.BOOLEAN); // Trade disabled
                                wrapper.passthrough(Type.INT); // Number of tools uses
                                wrapper.passthrough(Type.INT); // Maximum number of trade uses
                            }
                        }
                    }
                });
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_13.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.VAR_INT); // 1 - Slot ID
                map(Type.FLAT_ITEM, Type.FLAT_VAR_INT_ITEM); // 2 - Item
            }
        });

        protocol.registerOutgoing(ClientboundPackets1_13.DECLARE_RECIPES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int recipesNo = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < recipesNo; i++) {
                            wrapper.passthrough(Type.STRING); // Id
                            String type = wrapper.passthrough(Type.STRING);
                            if (type.equals("crafting_shapeless")) {
                                wrapper.passthrough(Type.STRING); // Group
                                int ingredientsNo = wrapper.passthrough(Type.VAR_INT);
                                for (int i1 = 0; i1 < ingredientsNo; i1++) {
                                    wrapper.write(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT, wrapper.read(Type.FLAT_ITEM_ARRAY_VAR_INT));
                                }
                                wrapper.write(Type.FLAT_VAR_INT_ITEM, wrapper.read(Type.FLAT_ITEM));
                            } else if (type.equals("crafting_shaped")) {
                                int ingredientsNo = wrapper.passthrough(Type.VAR_INT) * wrapper.passthrough(Type.VAR_INT);
                                wrapper.passthrough(Type.STRING); // Group
                                for (int i1 = 0; i1 < ingredientsNo; i1++) {
                                    wrapper.write(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT, wrapper.read(Type.FLAT_ITEM_ARRAY_VAR_INT));
                                }
                                wrapper.write(Type.FLAT_VAR_INT_ITEM, wrapper.read(Type.FLAT_ITEM));
                            } else if (type.equals("smelting")) {
                                wrapper.passthrough(Type.STRING); // Group
                                // Ingredient start
                                wrapper.write(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT, wrapper.read(Type.FLAT_ITEM_ARRAY_VAR_INT));
                                // Ingredient end
                                wrapper.write(Type.FLAT_VAR_INT_ITEM, wrapper.read(Type.FLAT_ITEM));
                                wrapper.passthrough(Type.FLOAT); // EXP
                                wrapper.passthrough(Type.VAR_INT); // Cooking time
                            }
                        }
                    }
                });
            }
        });

        protocol.registerIncoming(ServerboundPackets1_13.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // 0 - Window ID
                map(Type.SHORT); // 1 - Slot
                map(Type.BYTE); // 2 - Button
                map(Type.SHORT); // 3 - Action number
                map(Type.VAR_INT); // 4 - Mode
                map(Type.FLAT_VAR_INT_ITEM, Type.FLAT_ITEM); // 5 - Clicked Item
            }
        });
        protocol.registerIncoming(ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // 0 - Slot
                map(Type.FLAT_VAR_INT_ITEM, Type.FLAT_ITEM); // 1 - Clicked Item
            }
        });
    }
}
