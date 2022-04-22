package github.cheneykwok.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class HelloServer {

    public void start(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            Socket socket;
            while ((socket = server.accept()) != null) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                log.info("server receive message: {}", message.toString());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                message.setMessage("receive message");
                oos.writeObject(message);
                oos.flush();
                oos.close();
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HelloServer server = new HelloServer();
        server.start(10000);
    }
}
