package github.cheneykwok.annotations;

import java.lang.annotation.*;

/**
 * RPC service reference annotation
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {

    /**
     * service version
     */
    String version() default "";

    /**
     * service group
     */
    String group() default "";
}
