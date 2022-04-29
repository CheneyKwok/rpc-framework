package github.cheneykwok.provider;

import github.cheneykwok.config.RpcServerConfig;

public interface ServiceProvider {

    /**
     * @param rpcServerConfig rpc service related attributes
     */
    void addService(RpcServerConfig rpcServerConfig);

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     *
     * @param rpcServerConfig rpc service related attributes
     */
    void publishService(RpcServerConfig rpcServerConfig);
}
