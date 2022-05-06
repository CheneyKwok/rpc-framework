package github.cheneykwok.loadbalance;

import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.utils.CollectionUtil;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {

    public String selectServiceAddress(List<String> serviceAddress, RpcRequest rpcRequest) {
        if (CollectionUtil.isEmpty(serviceAddress)) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }
        return doSelect(serviceAddress, rpcRequest);
    }

    public abstract String doSelect(List<String> serviceAddress, RpcRequest rpcRequest);
}
