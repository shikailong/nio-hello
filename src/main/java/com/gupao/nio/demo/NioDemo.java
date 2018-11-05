package com.gupao.nio.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioDemo {

    public static void main(String[] args){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("D://NIO.txt"));
            FileChannel channel = fis.getChannel();

            ByteBuffer bb = ByteBuffer.allocate(10);

            channel.read(bb);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
