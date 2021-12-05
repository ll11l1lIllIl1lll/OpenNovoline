package viaversion.viarewind.utils;

import viaversion.viaversion.api.PacketWrapper;
import viaversion.viaversion.api.protocol.Protocol;
import viaversion.viaversion.exception.CancelException;

public class PacketUtil {

	public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol) {
		sendToServer(packet, packetProtocol, true);
	}

	public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline) {
		sendToServer(packet, packetProtocol, skipCurrentPipeline, false);
	}

	public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) {
		try {
			packet.sendToServer(packetProtocol, skipCurrentPipeline, currentThread);
		} catch (CancelException ignored) {
			;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean sendPacket(PacketWrapper packet, Class<? extends Protocol> packetProtocol) {
		return sendPacket(packet, packetProtocol, true);
	}

	public static boolean sendPacket(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline) {
		return sendPacket(packet, packetProtocol, true, false);
	}

	public static boolean sendPacket(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) {
		try {
			packet.send(packetProtocol, skipCurrentPipeline, currentThread);
			return true;
		} catch (CancelException ignored) {
			;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
