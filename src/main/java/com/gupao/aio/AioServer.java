package com.gupao.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioServer {

    private int port = 8080;
    public AioServer(int port) {
        this.port = port;
    }

    private void listen() throws IOException {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(this.port));

        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                client.read(buffer);
                buffer.flip();
                System.out.println(buffer.limit());
                System.out.println("completed" + new String(buffer.array(), 0, buffer.limit()/3));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("completed");
            }
        });
    }

    public static void main(String[] args){
        try {
            new AioServer(8080).listen();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
