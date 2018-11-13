package com.gupao.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class GpTomcat {

    public void start(int port)throws Exception{
//        ServerSocketChannel server = ServerSocketChannel.open();
//        server.bind(new InetSocketAddress(port));
//        server.configureBlocking(false);
//        Selector selector = Selector.open();
//        server.register(selector, SelectionKey.OP_ACCEPT);

        // 主从模型
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            // netty服务
            ServerBootstrap server = new ServerBootstrap();
            server.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {

                            // 无锁串行化编程
                            client.pipeline().addLast(new HttpResponseEncoder())
                                    .addLast(new HttpRequestDecoder())
                                    .addLast(new GpTomcatHandler());
                        }
                    })
                    // 配置信息
                    .option(ChannelOption.SO_BACKLOG, 128) // 针对主线程的配置
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 针对子线程的配置

            ChannelFuture future = server.bind(port).sync();
            System.out.println("GpTomcat已经启动！" + port);
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        try {
            new GpTomcat().start(8080);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
