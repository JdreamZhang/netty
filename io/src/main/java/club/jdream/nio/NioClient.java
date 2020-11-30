package club.jdream.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient {

    private Selector selector;

    public static void main(String[] args) throws IOException {
        NioClient nioClient = new NioClient();
        nioClient.initClient("127.0.0.1", 8080);
        nioClient.connect();
    }

    public void initClient(String ip, int port) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        this.selector = Selector.open();

        channel.connect(new InetSocketAddress(ip, port));
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    public void connect() throws IOException {
        while (true){
            selector.select();
            Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                it.remove();
                if(key.isConnectable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    if(channel.isConnectionPending()){
                        channel.finishConnect();
                    }
                    channel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.wrap("Hello, Server".getBytes());
                    channel.write(buffer);
                    channel.register(this.selector, SelectionKey.OP_READ);
                }else {
                    read(key);
                }
            }
        }
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(512);
        int len = channel.read(buffer);
        if (len != -1) {
            System.out.println("Client receive data: " + new String(buffer.array(), 0, len));
        }
    }
}
