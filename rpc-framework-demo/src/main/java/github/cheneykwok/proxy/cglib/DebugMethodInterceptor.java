package github.cheneykwok.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class DebugMethodInterceptor implements MethodInterceptor {

    /**
     *
     * @param o 被代理的对象
     * @param method 被代理的方法
     * @param objects 方法入参
     * @param methodProxy 用于调用原始方法
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("before method " + method.getName());
        Object result = methodProxy.invokeSuper(o, objects);
        System.out.println("after method " + method.getName());
        return result;
    }
}
