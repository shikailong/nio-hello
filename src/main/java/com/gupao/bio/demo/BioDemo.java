package com.gupao.bio.demo;

import java.io.*;

public class BioDemo {

    public static void main(String[] args) {

//        FileOutputStream fos = new FileOutputStream(new File("D://NIO.txt"));
//        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("D://NIO.txt")));

        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(new File("D://NIO.txt"));
            reader = new BufferedReader(new InputStreamReader(fis));
            String str = "";
//            while((str = reader.readLine()) != null){
//                System.out.println(new String(str.getBytes(), "utf-8"));
//            }
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
