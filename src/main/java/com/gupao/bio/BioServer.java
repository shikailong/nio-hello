package com.gupao.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    private static ServerSocket serverSocket = null;

    public BioServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("BIO监听已经启动，端口为" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listener() throws IOException {
        while (true){
            // 虽然写了一个死循环，如果一直没有客户端，这里一直不会往下走，这里一直处于阻塞状态
            Socket accept = serverSocket.accept();
            InputStream inputStream = accept.getInputStream();
            byte[] bytes = new byte[1024];
            int len = inputStream.read(bytes);
            if(len > 0){
                String msg = new String(bytes, 0, len);
                System.out.println(msg + "，字节数为：" + len);
            }
        }
    }

    public static void main(String[] args){
        BioServer bioServer = new BioServer(12345);
        try {
            bioServer.listener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
