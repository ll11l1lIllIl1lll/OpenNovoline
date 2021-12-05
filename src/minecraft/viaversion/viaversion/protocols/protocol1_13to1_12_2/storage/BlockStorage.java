package viaversion.viaversion.protocols.protocol1_13to1_12_2.storage;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import viaversion.viaversion.api.data.StoredObject;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.minecraft.Position;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockStorage extends StoredObject {
    private static final IntSet WHITELIST = new IntOpenHashSet(46, 1F);
    private final Map<Position, ReplacementData> blocks = new ConcurrentHashMap<>();

    static {
        // Flower pots
        WHITELIST.add(5266);

        // Add those red beds
        for (int i = 0; i < 16; i++) {
            WHITELIST.add(972 + i);
        }

        // Add the white banners
        for (int i = 0; i < 20; i++) {
            WHITELIST.add(6854 + i);
        }

        // Add the white wall banners
        for (int i = 0; i < 4; i++) {
            WHITELIST.add(7110 + i);
        }

        // Skeleton skulls
        for (int i = 0; i < 5; i++) {
            WHITELIST.add(5447 + i);
        }
    }

    public BlockStorage(UserConnection user) {
        super(user);
    }

    public void store(Position position, int block) {
        store(position, block, -1);
    }

    public void store(Position position, int block, int replacementId) {
        if (!WHITELIST.contains(block))
            return;

        blocks.put(position, new ReplacementData(block, replacementId));
    }

    public boolean isWelcome(int block) {
        return WHITELIST.contains(block);
    }

    public boolean contains(Position position) {
        return blocks.containsKey(position);
    }

    public ReplacementData get(Position position) {
        return blocks.get(position);
    }

    public ReplacementData remove(Position position) {
        return blocks.remove(position);
    }

    public static class ReplacementData {
        private int original;
        private int replacement;

        public ReplacementData(int original, int replacement) {
            this.original = original;
            this.replacement = replacement;
        }

        public int getOriginal() {
            return original;
        }

        public void setOriginal(int original) {
            this.original = original;
        }

        public int getReplacement() {
            return replacement;
        }

        public void setReplacement(int replacement) {
            this.replacement = replacement;
        }
    }
}
