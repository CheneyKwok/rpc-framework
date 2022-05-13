package github.cheneykwok;

import github.cheneykwok.annotations.RpcScan;
import github.cheneykwok.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@RpcScan(basePackage = "github.cheneykwok")
public class NettyServerMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer server = context.getBean(NettyRpcServer.class);
        server.start();
    }
}
