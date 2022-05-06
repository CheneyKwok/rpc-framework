package github.cheneykwok.loadbalance;

import github.cheneykwok.remoting.dto.RpcRequest;

import java.util.List;

/**
 * Interface to the load balancing policy
 */
public interface LoadBalance {

    /**
     * choose one from the list of exists service address list
     * @return selected service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
