package github.cheneykwok.spring;

import github.cheneykwok.annotations.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            System.out.println("===========================");
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
