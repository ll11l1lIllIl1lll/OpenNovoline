package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.util.LazyLoadBase;

final class NetworkSystem$1 extends LazyLoadBase {
   protected NioEventLoopGroup load() {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
   }
}
