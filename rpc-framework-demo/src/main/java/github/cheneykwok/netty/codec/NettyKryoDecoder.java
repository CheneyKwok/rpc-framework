package github.cheneykwok.netty.codec;

import github.cheneykwok.netty.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;

    private final Class<?> genericClass;

    private static final int BODY_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 确保 ByteBuf 可读字节有效
        if (byteBuf.readableBytes() >= BODY_LENGTH) {
            // 标记当前位置，以便后面重置 readIndex 的时候使用
            byteBuf.markReaderIndex();
            // 读取消息长度
            int length = byteBuf.readInt();
            // 确保消息长度有效并且 +ByteBuf 可读字节有效
            if (length < 0 || byteBuf.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            // 如果可读字节数小于消息长度的话，说明不是完整的，重置 readIndex
            if (byteBuf.readableBytes() < length) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            Object obj = serializer.deserialize(body, genericClass);
            list.add(obj);
            log.info("successful decode ByteBuf to Object");
        }
    }
}
