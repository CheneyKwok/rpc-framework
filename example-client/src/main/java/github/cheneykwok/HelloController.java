package github.cheneykwok;

import github.cheneykwok.annotations.RpcReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloController {

    @RpcReference(version = "3.0", group = "test")
    private HelloService helloService;

    @SneakyThrows
    public void test() {
        String result = helloService.hello(new Hello("test RPC annotation", "Hello"));
        log.info(result);
        Thread.sleep(5000);
        for (int i = 0; i < 10; i++) {
            result = helloService.hello(new Hello("test RPC annotation", "Hello"));
            log.info(result);
        }
    }
}
