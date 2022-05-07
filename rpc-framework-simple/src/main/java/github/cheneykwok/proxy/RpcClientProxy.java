package github.cheneykwok.proxy;

import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.enums.RpcErrorMessageEnum;
import github.cheneykwok.enums.RpcResponseCodeEnum;
import github.cheneykwok.exception.RpcException;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.remoting.dto.RpcResponse;
import github.cheneykwok.remoting.socket.SocketRpcClient;
import github.cheneykwok.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";
    private final RpcServiceConfig rpcServiceConfig;

    private final RpcRequestTransport rpcRequestTransport;

    public RpcClientProxy(RpcServiceConfig rpcServiceConfig, RpcRequestTransport rpcRequestTransport) {
        this.rpcServiceConfig = rpcServiceConfig;
        this.rpcRequestTransport = rpcRequestTransport;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = new RpcServiceConfig();
    }


    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoke method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }
        check(rpcResponse, rpcRequest);

        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        String interfaceName = INTERFACE_NAME + ":" + rpcRequest.getInterfaceName();
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, interfaceName);
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, interfaceName);
        }
        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, interfaceName);
        }
    }
}
