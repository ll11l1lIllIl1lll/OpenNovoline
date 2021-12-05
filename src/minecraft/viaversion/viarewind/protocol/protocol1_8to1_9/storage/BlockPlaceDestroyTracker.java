package viaversion.viarewind.protocol.protocol1_8to1_9.storage;

import viaversion.viaversion.api.data.StoredObject;
import viaversion.viaversion.api.data.UserConnection;

public class BlockPlaceDestroyTracker extends StoredObject {
	private long blockPlaced, lastMining;
	private boolean mining;

	public BlockPlaceDestroyTracker(UserConnection user) {
		super(user);
	}

	public long getBlockPlaced() {
		return blockPlaced;
	}

	public void place() {
		blockPlaced = System.currentTimeMillis();
	}

	public boolean isMining() {
		long time = System.currentTimeMillis()-lastMining;
		return mining && time<75 || time<75;
	}

	public void setMining(boolean mining) {
		this.mining = mining && getUser().get(EntityTracker.class).getPlayerGamemode()!=1;
		lastMining = System.currentTimeMillis();
	}

	public long getLastMining() {
		return lastMining;
	}

	public void updateMining() {
		if (this.isMining()) {
			lastMining = System.currentTimeMillis();
		}
	}

	public void setLastMining(long lastMining) {
		this.lastMining = lastMining;
	}
}
