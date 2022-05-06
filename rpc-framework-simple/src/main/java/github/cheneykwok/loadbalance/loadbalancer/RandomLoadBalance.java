package github.cheneykwok.loadbalance.loadbalancer;

import github.cheneykwok.loadbalance.AbstractLoadBalance;
import github.cheneykwok.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * random load balancing strategy
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    public String doSelect(List<String> serviceAddress, RpcRequest rpcRequest) {

        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
