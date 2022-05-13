package github.cheneykwok.remoting.transport.netty.client;

import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.remoting.constants.RpcConstants;
import github.cheneykwok.remoting.dto.RpcMessage;
import github.cheneykwok.remoting.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler {

    private final UnprocessedRequests unprocessedRequests;

    private final NettyRpcClient nettyRpcClient;

    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingleFactory.getInstance(UnprocessedRequests.class);
        this.nettyRpcClient = SingleFactory.getInstance(NettyRpcClient.class);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcMessage) {
            RpcMessage rpcMessage = (RpcMessage) msg;
            byte messageType = rpcMessage.getMessageType();
            if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                log.info("heart [{}]", rpcMessage.getData());
            } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                RpcResponse rpcResponse = (RpcResponse) rpcMessage.getData();
                unprocessedRequests.complete(rpcResponse);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
