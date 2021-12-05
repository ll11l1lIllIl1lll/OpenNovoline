package viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage;

import viaversion.viaversion.api.data.StoredObject;
import viaversion.viaversion.api.data.UserConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TabCompleteStorage extends StoredObject {
    public int lastId;
    public String lastRequest;
    public boolean lastAssumeCommand;
    public Map<UUID, String> usernames = new HashMap<>();

    public TabCompleteStorage(UserConnection user) {
        super(user);
    }
}
