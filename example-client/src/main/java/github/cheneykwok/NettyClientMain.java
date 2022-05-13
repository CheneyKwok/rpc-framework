package github.cheneykwok;

import github.cheneykwok.annotations.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"github.cheneykwok"})
public class NettyClientMain {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController controller = context.getBean(HelloController.class);
        controller.test();
    }
}
