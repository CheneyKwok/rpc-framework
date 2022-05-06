package github.cheneykwok.registry.zk;

import github.cheneykwok.enums.RpcErrorMessageEnum;
import github.cheneykwok.exception.RpcException;
import github.cheneykwok.extension.ExtensionLoader;
import github.cheneykwok.loadbalance.LoadBalance;
import github.cheneykwok.registry.ServiceDiscovery;
import github.cheneykwok.registry.zk.util.CuratorUtils;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.omg.PortableInterceptor.INACTIVE;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        String rpcServiceName = rpcRequest.getRpcServiceName();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully discovery service address: [{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
