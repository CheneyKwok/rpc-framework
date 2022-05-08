package github.cheneykwok;

import github.cheneykwok.annotations.RpcService;
import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.remoting.socket.SocketRpcServer;
import github.cheneykwok.serviceimpl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

//@ComponentScan(includeFilters = {@ComponentScan.Filter(value = RpcService.class)})
public class SocketServerMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SocketServerMain.class);
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(new HelloServiceImpl());
        SocketRpcServer server = new SocketRpcServer();
        server.registerServer(rpcServiceConfig);
        server.start();
    }
}
