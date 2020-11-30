package club.jdream.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true){
            System.out.println("Waiting for connect...");
            // blocked here to wait
            Socket socket = serverSocket.accept();
            System.out.println("There is a client to connect...");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    handle(socket);
                }
            });
            thread.start();
        }
    }

    public static void handle(Socket socket){
        System.out.println("Thread id = " + Thread.currentThread().getId());
        byte[] bytes = new byte[1024];

        System.out.println("Ready for read...");
        try {
            int read = socket.getInputStream().read(bytes);
            System.out.println("Read over ...");
            if(read != -1){
                System.out.println("Receive data from client is: " + new String(bytes, 0, read));
                System.out.println("Thread id is: " + Thread.currentThread().getId());
            }
            socket.getOutputStream().write("Hello, client".getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
