package github.cheneykwok.remoting.transport.netty.codec;

import github.cheneykwok.compress.Compress;
import github.cheneykwok.enums.CompressTypeEnum;
import github.cheneykwok.enums.SerializationTypeEnum;
import github.cheneykwok.extension.ExtensionLoader;
import github.cheneykwok.remoting.constants.RpcConstants;
import github.cheneykwok.remoting.dto.RpcMessage;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.remoting.dto.RpcResponse;
import github.cheneykwok.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * <pre>
 *   0    1    2    3    4         5    6    7    8    9            10      11         12   13   14   15  16
 *   +----+----+----+----+---------+----+----+----+----+------------+-------+----------+----+----+----+---+
 *   |   magic   code   | version |   full length     | messageType | codec | compress |    requestId     |
 *   +----------------------------------------------------------------------------------------------------+
 *   |                                                                                                    |
 *   |                                         body                                                       |
 *   |                                                                                                    |
 *   |                                        ... ...                                                     |
 *   +----------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 *
 * 消息解码器
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * lengthFieldOffset: magic code is 4B, version is 1B, so value is 5
     * lengthFieldLength: message length is 4B, so value is 4
     * lengthAdjustment:  full length include all data and read 9 bytes before, so value is -9
     * initialBytesToStrip: we need check magic code and version, so do not strip any bytes and value is 0
     */
    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     *
     * @param maxFrameLength    可接受数据最大长度
     * @param lengthFieldOffset 长度字段偏移量
     * @param lengthFieldLength 长度字段所占字节数
     * @param lengthAdjustment  长度补偿值
     * @param initialBytesToStrip 接受数据时需跳过的字节数
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decode;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("decode frame error!", e);
                    throw e;
                }finally {
                    frame.release();
                }
            }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf frame) {
        checkMagicNumber(frame);
        checkVersion(frame);
        int fullLength = frame.readInt();
        byte messageType = frame.readByte();
        byte codecType = frame.readByte();
        byte compressType = frame.readByte();
        int requestId = frame.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .codec(codecType)
                .compress(compressType)
                .requestId(requestId)
                .build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] body = new byte[bodyLength];
            frame.readBytes(body);
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            body = compress.compress(body);
            String codecName = SerializationTypeEnum.getName(codecType);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest rpcRequest = serializer.deserialize(body, RpcRequest.class);
                rpcMessage.setData(rpcRequest);
            } else {
                RpcResponse rpcResponse = serializer.deserialize(body, RpcResponse.class);
                rpcMessage.setData(rpcResponse);
            }
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf frame) {
        byte version = frame.readByte();
        if (version != RpcConstants.VERSION) {
            throw new IllegalArgumentException("version isn't compatible: " + version);
        }
    }

    private void checkMagicNumber(ByteBuf frame) {
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        frame.readBytes(tmp);
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
