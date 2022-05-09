package github.cheneykwok;

import github.cheneykwok.annotations.RpcScan;
import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.remoting.socket.SocketRpcServer;
import github.cheneykwok.serviceimpl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@RpcScan(basePackage = {"github.cheneykwok"})
//@ComponentScan(includeFilters = {@ComponentScan.Filter(value = RpcService.class)})
public class SocketServerMain {

    public static void main(String[] args) {
//        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
//        rpcServiceConfig.setService(new HelloServiceImpl());
//        SocketRpcServer server = new SocketRpcServer();
//        server.registerServer(rpcServiceConfig);
//        server.start();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SocketServerMain.class);
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        socketRpcServer.start();

    }
}
