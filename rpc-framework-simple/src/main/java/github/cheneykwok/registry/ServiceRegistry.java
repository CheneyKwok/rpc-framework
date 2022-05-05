package github.cheneykwok.registry;

import github.cheneykwok.extension.SPI;

import java.net.InetSocketAddress;

/**
 * service registration
 */
@SPI
public interface ServiceRegistry {

    /**
     * register service
     * @param rpcServiceName rpc service name
     * @param address service address
     */
    void registerService(String rpcServiceName, InetSocketAddress address);
}
