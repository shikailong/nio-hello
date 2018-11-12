package com.gupao.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class BioClient {

    public static void main(String[] args){
        int count = 10;
//        CountDownLatch countDownLatch = new CountDownLatch(count);

//        for (int i = 0; i < count; i++){
            new Thread(()->{
                try {
//                    countDownLatch.await();
                    Socket client = new Socket("localhost", 8080);
                    OutputStream os = client.getOutputStream();
                    String name = "娃哈哈窿";
                    os.write(name.getBytes());
                    os.close();
                    client.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

//        }

    }

}
