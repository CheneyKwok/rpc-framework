package github.cheneykwok.registry;

import github.cheneykwok.extension.SPI;
import github.cheneykwok.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {

    /**
     * look service by rpcServiceName
     *
     * @return server address
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
