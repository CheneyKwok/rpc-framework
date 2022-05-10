package github.cheneykwok;

import github.cheneykwok.annotations.RpcScan;
import github.cheneykwok.remoting.transport.socket.SocketRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
