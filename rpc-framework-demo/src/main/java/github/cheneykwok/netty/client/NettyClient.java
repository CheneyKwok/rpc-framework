package github.cheneykwok.netty.client;

import github.cheneykwok.netty.codec.NettyKryoDecoder;
import github.cheneykwok.netty.codec.NettyKryoEncoder;
import github.cheneykwok.netty.dto.RpcRequest;
import github.cheneykwok.netty.dto.RpcResponse;
import github.cheneykwok.netty.serialize.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {

    private final String host;

    private final int port;

    private static final Bootstrap b;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化资源 Bootstrap
    static {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer serializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /**
                         * 自定义序列化编解码器
                         */
                        // RpcResponse -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoDecoder(serializer, RpcResponse.class));
                        // ByteBuf -> RpcRequest
                        ch.pipeline().addLast(new NettyKryoEncoder(serializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 发送消息到服务端
     *
     * 1.通过 Bootstrap 对象连接服务端
     * 2.通过 Channel 向服务端发送消息 RpcRequest
     * 3.发送成功后，阻塞等待，直到 channel 关闭
     * 4.拿到服务端返回结果 RpcResponse
     *
     * @param rpcRequest 消息体
     * @return RpcResponse
     */
    RpcResponse sendMessage(RpcRequest rpcRequest) {
        ChannelFuture channelFuture;
        try {
            channelFuture = b.connect(host, port).sync();
            log.info("client connect {}", host + ":" + port);
            Channel channel = channelFuture.channel();
            log.info("send message");
            if (channel != null) {
                channel.writeAndFlush(rpcRequest)
                        .addListener(future -> {
                            if (future.isSuccess()) {
                                log.info("client send message: [{}]", rpcRequest.toString());
                            } else {
                                log.error("send failed:", future.cause());
                            }
                        });
                // 阻塞等待，直到 channel 关闭
                channel.closeFuture().sync();
                // 将服务端返回的数据也就是 RpcResponse 对象取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return channel.attr(key).get();
            }
        } catch (Exception e) {
            log.error("occur exception when connect server:", e);
        }
        return null;
    }

    public static void main(String[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("interface")
                .methodName("hello")
                .build();
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8888);
        for (int i = 0; i < 3; i++) {
            nettyClient.sendMessage(rpcRequest);
        }
        RpcResponse rpcResponse = nettyClient.sendMessage(rpcRequest);
        System.out.println(rpcResponse.toString());

    }


}
