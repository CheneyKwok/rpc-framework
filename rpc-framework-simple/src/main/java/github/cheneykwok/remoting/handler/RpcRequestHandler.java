package github.cheneykwok.remoting.handler;

import github.cheneykwok.enums.RpcErrorMessageEnum;
import github.cheneykwok.exception.RpcException;
import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.provider.ServiceProvider;
import github.cheneykwok.provider.impl.ZKServiceProviderImpl;
import github.cheneykwok.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingleFactory.getInstance(ZKServiceProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (Exception e) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE);
        }
        return result;
    }
}
