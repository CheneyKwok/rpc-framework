package github.cheneykwok.annotations;

import github.cheneykwok.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage();
}
