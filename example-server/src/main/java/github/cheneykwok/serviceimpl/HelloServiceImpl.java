package github.cheneykwok.serviceimpl;

import github.cheneykwok.Hello;
import github.cheneykwok.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class HelloServiceImpl implements HelloService {

    static {
        log.info("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到：{}", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回：{}", result);
        return result;
    }
}
