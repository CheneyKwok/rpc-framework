package github.cheneykwok.spring;

import github.cheneykwok.annotations.RpcScan;
import github.cheneykwok.annotations.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 自定义扫描注册器
 */
@Slf4j
public class CustomScannerRegistrar implements ResourceLoaderAware, ImportBeanDefinitionRegistrar {

    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";

    private static final String SPRING_BEAN_BASE_PACKAGE = "github.cheneykwok";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(RpcScan.class.getName());
        AnnotationAttributes rpcAnnotationAttributes = AnnotationAttributes.fromMap(annotationAttributes);
        String[] rpcScanBasePackages = new String[0];
        if (rpcAnnotationAttributes !=  null) {
            rpcScanBasePackages = rpcAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata)annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        CustomScanner rpcServiceScanner = new CustomScanner(registry, RpcService.class);
        CustomScanner springBeanScanner = new CustomScanner(registry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);
        int rpcServiceNum = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceNum);


    }
}
