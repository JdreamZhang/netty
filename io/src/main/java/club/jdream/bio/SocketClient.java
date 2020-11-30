package club.jdream.bio;

import java.io.IOException;
import java.net.Socket;

public class SocketClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket( "127.0.0.1",8080);

        System.out.println("Start send data to sever");
        socket.getOutputStream().write("hello, server".getBytes());
        socket.getOutputStream().flush();
        System.out.println("Send data to server over...");

        byte[] bytes = new byte[1024];
        int read = socket.getInputStream().read(bytes);
        System.out.println("Receive data from sever: " + new String(bytes, 0, read));
        socket.close();
    }

}
