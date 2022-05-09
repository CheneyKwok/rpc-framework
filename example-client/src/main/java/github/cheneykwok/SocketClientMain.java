package github.cheneykwok;

import github.cheneykwok.annotations.RpcScan;
import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.proxy.RpcClientProxy;
import github.cheneykwok.remoting.socket.SocketRpcClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"github.cheneykwok"})
public class SocketClientMain {

    public static void main(String[] args) {
//        RpcClientProxy rpcClientProxy = new RpcClientProxy(new RpcServiceConfig(), new SocketRpcClient());
//        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
//        String result = helloService.hello(new Hello("111", "222"));
//        System.out.println(result);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SocketClientMain.class);
        HelloController helloController = context.getBean(HelloController.class);
        helloController.test();
    }
}
