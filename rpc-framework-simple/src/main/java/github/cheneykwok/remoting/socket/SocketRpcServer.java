package github.cheneykwok.remoting.socket;

import github.cheneykwok.config.CustomShutdownHook;
import github.cheneykwok.config.RpcServiceConfig;
import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.provider.ServiceProvider;
import github.cheneykwok.provider.impl.ZKServiceProviderImpl;
import github.cheneykwok.utils.threadpool.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;

    private final ServiceProvider serviceProvider;

    public static final int port = 8811;

    public SocketRpcServer() {
        this.threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        this.serviceProvider = SingleFactory.getInstance(ZKServiceProviderImpl.class);
    }

    public void registerServer(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig, port);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()){
            String host = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(host, port));
            CustomShutdownHook.me().clearRegistry(port);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("client connected [{}]", serverSocket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
