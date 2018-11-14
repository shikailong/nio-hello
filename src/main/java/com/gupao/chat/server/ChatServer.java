package com.gupao.chat.server;

import com.gupao.chat.server.handler.HttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServer {

    private int port = 8080;
    public void start(){

        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 所有自定义业务
                            ch.pipeline()
                                    // 解码编码都可以进行
                                    .addLast(new HttpServerCodec())
                                    // 可以接收到http最大的头信息
                                    .addLast(new HttpObjectAggregator(64 * 1024))
                                    // 用于处理文件流的handler
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpHandler());
                        }
                    });

            ChannelFuture future = server.bind(this.port).sync();
            System.out.println("聊天服务器已经启动：" + this.port);
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new ChatServer().start();
    }

}
