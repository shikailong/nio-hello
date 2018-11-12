package com.gupao.nio.buffer;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

public class BufferProgram {

    public static void main(String[] args) throws IOException {

        FileInputStream fis = new FileInputStream("e://test.txt");
        FileChannel channel = fis.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(10);
        channel.read(buffer);
        output("调用read()" , buffer);

        buffer.flip();
        output("调用filp" , buffer);

        while (buffer.remaining() > 0){
            byte b = buffer.get();
        }

        output("调用get()", buffer);


        channel.close();

    }

    public static void output(String msg, ByteBuffer buffer){
        System.out.println(msg);
        System.out.println("capacity:" + buffer.capacity() + ",position:" + buffer.position() + ",limit：" + buffer.limit());
    }

}
