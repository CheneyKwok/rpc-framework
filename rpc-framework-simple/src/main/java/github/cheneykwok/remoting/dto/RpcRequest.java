package github.cheneykwok.remoting.dto;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -7072350265910398307L;

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] paramTypes;

    /**
     * 为后续不兼容升级提供可能
     */
    private String version;

    /**
     * 用于处理一个接口有多个类实现的情况
     */
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
