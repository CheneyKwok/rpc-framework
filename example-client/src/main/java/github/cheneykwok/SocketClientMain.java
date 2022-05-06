package github.cheneykwok;

import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.remoting.socket.SocketRpcClient;

public class SocketClientMain {

    public static void main(String[] args) {
        SocketRpcClient client = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
    }
}
