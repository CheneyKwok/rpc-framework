package github.cheneykwok.proxy.jdk;

import java.lang.reflect.Proxy;

/**
 * JDK 动态代理
 * 只能代理实现类接口的类
 */
public class JDKProxyFactory {

    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new DebugInvocationHandler(target)
        );
    }

    public static void main(String[] args) {
        SmsService smsService = (SmsService) getProxy(new SmsServiceImpl());
        smsService.send("Java");
    }
}
