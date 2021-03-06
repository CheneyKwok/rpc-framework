package github.cheneykwok.remoting.transport;

import github.cheneykwok.extension.SPI;
import github.cheneykwok.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {

    /**
     * send rpc requset to server and get result
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
