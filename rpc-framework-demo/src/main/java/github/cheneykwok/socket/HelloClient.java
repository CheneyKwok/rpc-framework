package github.cheneykwok.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

@Slf4j
public class HelloClient {

    public Object send(Message message, String host, int port) {
        Socket socket ;
        try {
            socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            message = (Message) ois.readObject();
            log.info(message.getMessage());
            oos.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static void main(String[] args) {
        Message message = new Message("hello");
        HelloClient client = new HelloClient();
        client.send(message, "127.0.0.1", 10000);
    }
}
