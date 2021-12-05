package viaversion.viaversion.protocols.protocol1_9to1_8.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

public class ChatRewriter {
    /**
     * Rewrite chat being sent to the client so that gamemode issues don't occur.
     *
     * @param obj  The json object being sent by the server
     * @param user The player involved. (Required for Gamemode info)
     */
    public static void toClient(JsonObject obj, UserConnection user) {
        //Check gamemode change
        if (obj.get("translate") != null && obj.get("translate").getAsString().equals("gameMode.changed")) {
            String gameMode = user.get(EntityTracker1_9.class).getGameMode().getText();

            JsonObject gameModeObject = new JsonObject();
            gameModeObject.addProperty("text", gameMode);
            gameModeObject.addProperty("color", "gray");
            gameModeObject.addProperty("italic", true);

            JsonArray array = new JsonArray();
            array.add(gameModeObject);

            obj.add("with", array);
        }
    }
}
