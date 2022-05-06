package github.cheneykwok;

import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.remoting.socket.SocketRpcServer;
import github.cheneykwok.serviceimpl.HelloServiceImpl;

public class SocketServerMain {

    public static void main(String[] args) {
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(new HelloServiceImpl());
        SocketRpcServer server = new SocketRpcServer();
        server.registerServer(rpcServiceConfig);
        server.start();
    }
}
