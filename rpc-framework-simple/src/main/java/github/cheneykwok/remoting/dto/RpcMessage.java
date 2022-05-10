package github.cheneykwok.remoting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcMessage {

    /**
     * 消息类型（1B）
     */
    private byte messageType;

    /**
     * 序列化类型（1B）
     */
    private byte codec;

    /**
     * 压缩类型（1B）
     */
    private byte compress;

    /**
     * 请求的 Id（4B）
     */
    private int requestId;

    /**
     * data body
     */
    private Object data;
}
