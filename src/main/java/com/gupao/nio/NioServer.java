package com.gupao.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
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
                SelectionKey key = iterator.next();
                iterator.remove();
                process(key);
            }
        }
    }

    private void process(SelectionKey key) throws IOException {

        // 判断客户端确定已经
        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();

            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);

            key.interestOps(SelectionKey.OP_ACCEPT);
            client.write(charset.encode("请输入你的昵称"));
        }

        if(key.isReadable()){

            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder sb = new StringBuilder();
            try {

                while (client.read(buffer) > 0){
                    buffer.flip();
                    sb.append(charset.decode(buffer));
                }

                key.interestOps(SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
                key.cancel();
                if(key.channel() != null){
                    key.channel().close();
                }
            }

            if(sb.length() > 0){
                String[] arrayContent = sb.toString().split(USER_CONTENT_SPLIT);
                if(arrayContent != null && arrayContent.length == 1){
                    String nickName = arrayContent[0];
                    if(users.contains(nickName)){
                        client.write(charset.encode(USER_EXIST));
                    }else{
                        users.add(nickName);
                        int onlineCount = onLineCount();
                        String message = "欢迎" + nickName + " 进入聊天室！当前在线人数：" + onlineCount ;
                        briCast(null, message);
                    }
                }else if(arrayContent != null && arrayContent.length > 1){
                    String nickName = arrayContent[0];
                    String message = sb.substring(nickName.length() + USER_CONTENT_SPLIT.length()).toString();
                    message = nickName + "说：" + message;
                    if(users.contains(nickName)){
                        briCast(null, message);
                    }

                }
            }
        }
    }

    private void briCast(Object o, String message) {

    }


    private int onLineCount() {
        int res = 0;
        for(SelectionKey key : selector.keys()){
            SelectableChannel target = key.channel();
            if(target instanceof SocketChannel){
                res ++;
            }
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        new NioServer(8080).listener();
    }
}
