package github.cheneykwok.annotations;

import java.lang.annotation.*;

/**
 *
 * RPC service register annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface RpcService {

    /**
     * service version
     */
    String version() default "";

    /**
     * service group
     */
    String group() default "";
}
