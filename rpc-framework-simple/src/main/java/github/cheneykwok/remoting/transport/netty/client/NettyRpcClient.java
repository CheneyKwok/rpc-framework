package github.cheneykwok.remoting.transport.netty.client;

import github.cheneykwok.enums.CompressTypeEnum;
import github.cheneykwok.enums.SerializationTypeEnum;
import github.cheneykwok.extension.ExtensionLoader;
import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.registry.ServiceDiscovery;
import github.cheneykwok.remoting.constants.RpcConstants;
import github.cheneykwok.remoting.dto.RpcMessage;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.remoting.dto.RpcResponse;
import github.cheneykwok.remoting.transport.RpcRequestTransport;
import github.cheneykwok.remoting.transport.netty.codec.RpcMessageDecoder;
import github.cheneykwok.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    private final ChannelProvider channelProvider;

    private final UnprocessedRequests unprocessedRequests;

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });

        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.channelProvider = SingleFactory.getInstance(ChannelProvider.class);
        this.unprocessedRequests = SingleFactory.getInstance(UnprocessedRequests.class);
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> future = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                log.info("client connected [{}] successfully", inetSocketAddress.toString());
                future.complete(f.channel());
            } else {
                throw new IllegalStateException("connect failed " + f.cause());
            }
        });
        return future.get();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {

        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        InetSocketAddress serviceUrl = serviceDiscovery.lookupService(rpcRequest);
        Channel channel = getChannel(serviceUrl);
        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .messageType(RpcConstants.REQUEST_TYPE)
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .codec(SerializationTypeEnum.HESSIAN.getCode())
                    .data(rpcRequest)
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    private Channel getChannel(InetSocketAddress serviceUrl) {
        Channel channel = channelProvider.get(serviceUrl);
        if (channel == null) {
            channel = doConnect(serviceUrl);
            channelProvider.set(serviceUrl, channel);
        }
        return channel;
    }
}
