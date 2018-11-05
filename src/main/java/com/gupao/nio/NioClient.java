package com.gupao.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NioClient {

    private final InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8088);
    private Selector selector = null;
    private SocketChannel client = null;

    private String nickName = "";
    private Charset charset = Charset.forName("utf-8");

    private static String USER_EXIST = "系统提示：改昵称已经存在，请换一个昵称";
    private static String USER_CONTENT_SPLIT = "#@#";

    public NioClient() throws IOException {
        selector = Selector.open();
        client = SocketChannel.open(serverAddress);
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    public void session(){
        new Reader().start();
        new Writer().start();
    }

    class Reader extends Thread{
        @Override
        public void run() {

        }
    }
    class Writer extends Thread{
        @Override
        public void run() {

        }
    }

}
