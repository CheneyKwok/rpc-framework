package github.cheneykwok.netty.codec;

import github.cheneykwok.netty.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * 编码器
 *
 * 网络传输需要通过字节流来实现，ByteBuf 可以看作是 Netty 提供的字节数据的容器，使用它会让我们更方便地处理字节数据
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private final Serializer serializer;

    private final Class<?> genericClass;


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)) {
            // 序列化对象
            byte[] body = serializer.serialize(o);
            // 写入消息对应的长度，writeIndex + 4
            int length = body.length;
            byteBuf.writeInt(length);
            // 将字节数组写入 ByteBuf 对象中
            byteBuf.writeBytes(body);
        }
    }
}
