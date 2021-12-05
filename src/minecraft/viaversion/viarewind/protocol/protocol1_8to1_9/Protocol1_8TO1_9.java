package viaversion.viarewind.protocol.protocol1_8to1_9;

import com.google.common.collect.ImmutableSet;
import viaversion.viarewind.protocol.protocol1_8to1_9.packets.EntityPackets;
import viaversion.viarewind.protocol.protocol1_8to1_9.packets.InventoryPackets;
import viaversion.viarewind.protocol.protocol1_8to1_9.packets.PlayerPackets;
import viaversion.viarewind.protocol.protocol1_8to1_9.packets.ScoreboardPackets;
import viaversion.viarewind.protocol.protocol1_8to1_9.packets.SpawnPackets;
import viaversion.viarewind.protocol.protocol1_8to1_9.packets.WorldPackets;
import viaversion.viarewind.protocol.protocol1_8to1_9.storage.BlockPlaceDestroyTracker;
import viaversion.viarewind.protocol.protocol1_8to1_9.storage.BossBarStorage;
import viaversion.viarewind.protocol.protocol1_8to1_9.storage.Cooldown;
import viaversion.viarewind.protocol.protocol1_8to1_9.storage.EntityTracker;
import viaversion.viarewind.protocol.protocol1_8to1_9.storage.Levitation;
import viaversion.viarewind.protocol.protocol1_8to1_9.storage.PlayerPosition;
import viaversion.viarewind.protocol.protocol1_8to1_9.storage.Windows;
import viaversion.viarewind.utils.Ticker;
import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.data.UserConnection;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.api.remapper.ValueTransformer;
import viaversion.viaversion.api.type.Type;
import viaversion.viaversion.packets.State;
import viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

import java.util.Timer;

public class Protocol1_8TO1_9 extends Protocol {
	public static final Timer TIMER = new Timer("ViaRewind-1_8TO1_9", true);
	public static final ImmutableSet<Object> VALID_ATTRIBUTES;

	static {
		ImmutableSet.Builder<Object> builder = ImmutableSet.builder();
		builder.add("generic.maxHealth");
		builder.add("generic.followRange");
		builder.add("generic.knockbackResistance");
		builder.add("generic.movementSpeed");
		builder.add("generic.attackDamage");
		builder.add("horse.jumpStrength");
		builder.add("zombie.spawnReinforcements");
		VALID_ATTRIBUTES = builder.build();
	}

	public static final ValueTransformer<Double, Integer> TO_OLD_INT = new ValueTransformer<Double, Integer>(Type.INT) {
		public Integer transform(PacketWrapper wrapper, Double inputValue) {
			return (int) (inputValue * 32.0D);
		}
	};
	public static final ValueTransformer<Float, Byte> DEGREES_TO_ANGLE = new ValueTransformer<Float, Byte>(Type.BYTE) {
		@Override
		public Byte transform(PacketWrapper packetWrapper, Float degrees) throws Exception {
			return (byte) ((degrees / 360F) * 256);
		}
	};

	@Override
	protected void registerPackets() {
		EntityPackets.register(this);
		InventoryPackets.register(this);
		PlayerPackets.register(this);
		ScoreboardPackets.register(this);
		SpawnPackets.register(this);
		WorldPackets.register(this);

		//Keep Alive
		this.registerOutgoing(State.PLAY, 0x1F, 0x00);

		//Keep Alive
		this.registerIncoming(State.PLAY, 0x0B, 0x00);
	}

	@Override
	public void init(UserConnection userConnection) {
		Ticker.init();

		userConnection.put(new Windows(userConnection));
		userConnection.put(new EntityTracker(userConnection));
		userConnection.put(new Levitation(userConnection));
		userConnection.put(new PlayerPosition(userConnection));
		userConnection.put(new Cooldown(userConnection));
		userConnection.put(new BlockPlaceDestroyTracker(userConnection));
		userConnection.put(new BossBarStorage(userConnection));
		userConnection.put(new ClientWorld(userConnection));
	}
}
