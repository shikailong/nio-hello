package com.gupao.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private int port = 8088;
    private Charset charset = Charset.forName("utf-8");

    private static Set<String> users = new HashSet<>();

    private static String USER_EXIST = "系统提示：改昵称已经存在，请换一个昵称";
    private static String USER_CONTENT_SPLIT = "#@#";

    private Selector selector = null;

    public NioServer(int port) throws IOException {
        this.port = port;
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(this.port));
        server.configureBlocking(false);

        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端口已经启动，端口为" + this.port);
    }

    public void listener() throws IOException {
        // 这里不会阻塞
        while (true) {
            int wait = selector.select();
            if (wait == 0){
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()){
                SelectionKey next = iterator.next();
                iterator.remove();
                process(next);
            }
        }
    }

    private void process(SelectionKey next) {
        SelectableChannel channel = next.channel();
        boolean blocking = channel.isBlocking();
    }


}
