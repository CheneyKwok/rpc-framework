package github.cheneykwok.remoting.transport.netty.server;

import github.cheneykwok.enums.CompressTypeEnum;
import github.cheneykwok.enums.RpcResponseCodeEnum;
import github.cheneykwok.enums.SerializationTypeEnum;
import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.remoting.constants.RpcConstants;
import github.cheneykwok.remoting.dto.RpcMessage;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.remoting.dto.RpcResponse;
import github.cheneykwok.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter implements RpcConstants {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingleFactory.getInstance(RpcRequestHandler.class);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("server receive msg: [{}]", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.HESSIAN.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                if (messageType == HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(PONG);
                } else {
                    RpcRequest rpcRequest = (RpcRequest)((RpcMessage) msg).getData();
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcMessage.setMessageType(RESPONSE_TYPE);
                    RpcResponse<Object> rpcResponse;
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                         rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                    } else {
                         rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                         log.error("not writeable now, message dropped");
                    }
                    rpcMessage.setData(rpcResponse);
                    ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

                }
            }
        }finally {
            // Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
