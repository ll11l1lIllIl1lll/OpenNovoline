package viaversion.viabackwards.protocol.protocol1_10to1_11;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class PotionSplashHandler {

	private static final Int2IntMap DATA = new Int2IntOpenHashMap(14, 1F);

	static {
		DATA.defaultReturnValue(-1);
		DATA.put(2039713, 5); // night vision
		DATA.put(8356754, 7); // invisibility
		DATA.put(2293580, 9); // jump boost
		DATA.put(14981690, 12); // fire resistance
		DATA.put(8171462, 14); // swiftness
		DATA.put(5926017, 17); // slowness
		DATA.put(3035801, 19); // water breathing
		DATA.put(16262179, 21); // instant health
		DATA.put(4393481, 23); // instant damage
		DATA.put(5149489, 25); // poison
		DATA.put(13458603, 28); // regeneration
		DATA.put(9643043, 31); // strength
		DATA.put(4738376, 34); // weakness
		DATA.put(3381504, 36); // luck
	}

	public static int getOldData(int data) {
		return DATA.get(data);
	}
}
