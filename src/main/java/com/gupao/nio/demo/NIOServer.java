package com.gupao.nio.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    private int port = 8080;
    private InetSocketAddress address = null;

    private Selector selector;

    public NIOServer(int port) {
        this.port = port;
//        address = new
        try {
            this.address = new InetSocketAddress(this.port);

            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(address);
            // 默认为阻塞，手动设置为非阻塞
            server.configureBlocking(false);

            this.selector = Selector.open();
            server.register(this.selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器准备就绪！监听端口是" + this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        try {
            while (true){
                int wait = this.selector.select();
                if(wait == 0){
                    continue;
                }

                Set<SelectionKey> keys = this.selector.selectedKeys();

                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    process(key);

                    iterator.remove();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        }else if(key.isReadable()){
            SocketChannel client = (SocketChannel)key.channel();
            int len = client.read(buffer);
            if(len > 0){
                buffer.flip();
                String content = new String(buffer.array(), 0, len);
                System.out.println(content);
                client.register(selector, SelectionKey.OP_WRITE);
            }
            buffer.clear();
        }else if(key.isWritable()){
            SocketChannel client = (SocketChannel)key.channel();
            client.write(buffer.wrap("hello world".getBytes()));
            client.close();
        }

    }

    public static void main(String[] args) {
        NIOServer server = new NIOServer(8080);
        server.listen();

    }

}
