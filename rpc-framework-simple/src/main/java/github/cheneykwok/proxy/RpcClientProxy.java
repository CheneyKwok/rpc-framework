package github.cheneykwok.proxy;

import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcServiceConfig rpcServiceConfig) {
        this.rpcServiceConfig = rpcServiceConfig;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoke method: [{}]", method.getName());
        RpcRequest.builder()
                .methodName(method.getName())
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .group()

        return null;
    }
}
