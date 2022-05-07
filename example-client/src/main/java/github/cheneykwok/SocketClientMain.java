package github.cheneykwok;

import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.proxy.RpcClientProxy;
import github.cheneykwok.remoting.socket.SocketRpcClient;

public class SocketClientMain {

    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy(new RpcServiceConfig(), new SocketRpcClient());
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String result = helloService.hello(new Hello("111", "222"));
        System.out.println(result);
    }
}
