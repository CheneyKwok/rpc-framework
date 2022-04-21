package github.cheneykwok.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class HelloServer {

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
