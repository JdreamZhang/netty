package club.jdream.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            System.out.println("Wait for event happen...");
            // Polling the key in the monitoring channel, select() is blocked, accept() is also blocked
            selector.select();

            System.out.println("Event happened...");
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                it.remove();
                handle(key);
            }
        }
    }

    private static void handle(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            System.out.println("Client connect event happened...");
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = ssc.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        }else if(key.isReadable()){
            System.out.println("Client send data read in...");
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = socketChannel.read(byteBuffer);
            if(len != -1){
                System.out.println("Read data from client : " + new String(byteBuffer.array(), 0, len));
            }
            ByteBuffer writeBuffer = ByteBuffer.wrap("Hello, client".getBytes());
            socketChannel.write(writeBuffer);
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }else if(key.isWritable()){
            SocketChannel sc = (SocketChannel) key.channel();
            System.out.println("write event");
            key.interestOps(SelectionKey.OP_READ);
        }
    }

}
