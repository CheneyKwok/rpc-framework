package github.cheneykwok.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceConfig {

    private String version = "";

    /**
     * 当接口有多个实现类时，通过 group 区分
     */
    private String group = "";

    /**
     * 目标服务
     */
    private Object service;

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

}
