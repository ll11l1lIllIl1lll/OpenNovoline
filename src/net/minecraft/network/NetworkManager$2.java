package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.epoll.EpollEventLoopGroup;
import net.minecraft.util.LazyLoadBase;

final class NetworkManager$2 extends LazyLoadBase {
   protected EpollEventLoopGroup load() {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
   }
}
