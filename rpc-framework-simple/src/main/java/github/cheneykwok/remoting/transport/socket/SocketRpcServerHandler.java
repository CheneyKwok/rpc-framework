package github.cheneykwok.remoting.transport.socket;

import github.cheneykwok.factory.SingleFactory;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.remoting.dto.RpcResponse;
import github.cheneykwok.remoting.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


@Slf4j
public class SocketRpcServerHandler implements Runnable {

    private final Socket socket;

    private final RpcRequestHandler rpcRequestHandler;

    public SocketRpcServerHandler(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingleFactory.getInstance(RpcRequestHandler.class);
    }


    @Override
    public void run() {
        log.info("server method message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            oos.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            oos.flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
