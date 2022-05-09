package github.cheneykwok.spring;

import github.cheneykwok.annotations.RpcReference;
import github.cheneykwok.annotations.RpcService;
import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.provider.ServiceProvider;
import github.cheneykwok.provider.impl.ZKServiceProviderImpl;
import github.cheneykwok.proxy.RpcClientProxy;
import github.cheneykwok.remoting.socket.SocketRpcClient;
import github.cheneykwok.remoting.socket.SocketRpcServer;
import github.cheneykwok.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;

    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingleFactory.getInstance(ZKServiceProviderImpl.class);
        this.rpcClient = new SocketRpcClient();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService rpcServiceAnnotation = bean.getClass().getAnnotation(RpcService.class);
            String version = rpcServiceAnnotation.version();
            String group = rpcServiceAnnotation.group();
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .service(bean)
                    .version(version)
                    .group(group)
                    .build();
            serviceProvider.publishService(rpcServiceConfig, SocketRpcServer.port);

        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        for (Field declaredField : beanClass.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcReferenceAnnotation = declaredField.getAnnotation(RpcReference.class);
                String version = rpcReferenceAnnotation.version();
                String group = rpcReferenceAnnotation.group();
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(group)
                        .version(version)
                        .build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcServiceConfig, rpcClient);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    log.error("reference Rpc service fail [{}]:{}", declaredField.getType(), e.getMessage());
                }
            }
        }
        return bean;
    }
}
