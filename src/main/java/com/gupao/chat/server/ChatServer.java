package com.gupao.chat.server;

import com.gupao.chat.protocol.IMDecoder;
import com.gupao.chat.protocol.IMEncoder;
import com.gupao.chat.server.handler.HttpHandler;
import com.gupao.chat.server.handler.SocketHandler;
import com.gupao.chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
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
//                    .channel(null) // 这是boss主线程中调用，可以用于权限控制
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 所有自定义业务
                            ch.pipeline()

                                    // =============用来支持http协议的=============
                                    // 解码编码都可以进行
                                    .addLast(new HttpServerCodec())
                                    // 可以接收到http最大的头信息
                                    .addLast(new HttpObjectAggregator(64 * 1024))
                                    // 用于处理文件流的handler
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpHandler());

                            // =============用来支持websocket协议的=============
                            ch.pipeline()
                                    .addLast(new WebSocketServerProtocolHandler("/im"))
                                    .addLast(new WebSocketHandler());

                            // =============用来支持自定义socket协议的=============
                            ch.pipeline()
                                    .addLast(new IMDecoder())
                                    .addLast(new IMEncoder())
                                    .addLast(new SocketHandler());

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
