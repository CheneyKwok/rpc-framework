package github.cheneykwok.remoting.socket;

import github.cheneykwok.enums.RpcErrorMessageEnum;
import github.cheneykwok.exception.RpcException;
import github.cheneykwok.extension.ExtensionLoader;
import github.cheneykwok.registry.ServiceDiscovery;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.remoting.transport.RpcRequestTransport;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket();
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            socket.connect(inetSocketAddress);
            oos.writeObject(rpcRequest);
            return ois.readObject();
        } catch (Exception e) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE);
        }
    }
}
