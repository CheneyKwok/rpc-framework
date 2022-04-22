package github.cheneykwok.netty.server;

import github.cheneykwok.netty.dto.RpcRequest;
import github.cheneykwok.netty.dto.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcRequest rpcRequest = (RpcRequest) msg;
            log.info("server receive msg:[{}], times:[{}]", rpcRequest, atomicInteger.getAndIncrement());
            RpcResponse rpcResponse = RpcResponse.builder()
                    .message("message from server")
                    .build();
            ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception", cause);
        ctx.close();
    }
}
