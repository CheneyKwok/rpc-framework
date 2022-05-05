package github.cheneykwok.provider;

import github.cheneykwok.config.RpcServiceConfig;

public interface ServiceProvider {

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     *
     * @param rpcServiceConfig rpc service related attributes
     * @param port service port
     */
    void publishService(RpcServiceConfig rpcServiceConfig, int port);

}
