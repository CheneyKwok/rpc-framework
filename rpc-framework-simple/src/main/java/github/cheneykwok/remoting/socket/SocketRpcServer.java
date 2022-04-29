package github.cheneykwok.remoting.socket;

import github.cheneykwok.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;

    private final ServiceProvider serviceProvider;

    public SocketRpcServer(ExecutorService threadPool, ServiceProvider serviceProvider) {
        this.threadPool = threadPool;
        this.serviceProvider = serviceProvider;
    }
}
