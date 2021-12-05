package viaversion.viaversion.protocols.protocol1_12_1to1_12;

import viaversion.viaversion.api.protocol.ServerboundPacketType;

public enum ServerboundPackets1_12_1 implements ServerboundPacketType {

    TELEPORT_CONFIRM, // 0x00
    TAB_COMPLETE, // 0x01
    CHAT_MESSAGE, // 0x02
    CLIENT_STATUS, // 0x03
    CLIENT_SETTINGS, // 0x04
    WINDOW_CONFIRMATION, // 0x05
    CLICK_WINDOW_BUTTON, // 0x06
    CLICK_WINDOW, // 0x07
    CLOSE_WINDOW, // 0x08
    PLUGIN_MESSAGE, // 0x09
    INTERACT_ENTITY, // 0x0A
    KEEP_ALIVE, // 0x0B
    PLAYER_MOVEMENT, // 0x0C
    PLAYER_POSITION, // 0x0D
    PLAYER_POSITION_AND_ROTATION, // 0x0E
    PLAYER_ROTATION, // 0x0F
    VEHICLE_MOVE, // 0x10
    STEER_BOAT, // 0x11
    CRAFT_RECIPE_REQUEST, // 0x12
    PLAYER_ABILITIES, // 0x13
    PLAYER_DIGGING, // 0x14
    ENTITY_ACTION, // 0x15
    STEER_VEHICLE, // 0x16
    RECIPE_BOOK_DATA, // 0x17
    RESOURCE_PACK_STATUS, // 0x18
    ADVANCEMENT_TAB, // 0x19
    HELD_ITEM_CHANGE, // 0x1A
    CREATIVE_INVENTORY_ACTION, // 0x1B
    UPDATE_SIGN, // 0x1C
    ANIMATION, // 0x1D
    SPECTATE, // 0x1E
    PLAYER_BLOCK_PLACEMENT, // 0x1F
    USE_ITEM, // 0x20
}
