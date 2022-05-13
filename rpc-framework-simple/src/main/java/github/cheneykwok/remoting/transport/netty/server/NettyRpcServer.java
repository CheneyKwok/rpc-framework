package github.cheneykwok.remoting.transport.netty.server;


import github.cheneykwok.config.CustomShutdownHook;
import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.provider.ServiceProvider;
import github.cheneykwok.provider.impl.ZKServiceProviderImpl;
import github.cheneykwok.remoting.transport.netty.codec.RpcMessageDecoder;
import github.cheneykwok.remoting.transport.netty.codec.RpcMessageEncoder;
import github.cheneykwok.utils.threadpool.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyRpcServer {

    public static final int PORT = 9998;

    private final ServiceProvider serviceProvider = SingleFactory.getInstance(ZKServiceProviderImpl.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig, PORT);
    }

    public void start() {
        CustomShutdownHook.me().clearRegistry(PORT);
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventLoopGroup serviceHandlerGroup = new DefaultEventLoopGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false));
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 启用 Nagle 算法，该算法的作用是尽可能的发送大数据块，减少网络传输
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 启用 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，可适当调大参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                        }
                    });

            // 绑定端口，同步等待绑定成功
            ChannelFuture channelFuture = b.bind(InetAddress.getLocalHost().getHostAddress(), PORT).sync();
            // 等待服务器监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("occur exception when start server: ", e);
        }finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }

    }
}
