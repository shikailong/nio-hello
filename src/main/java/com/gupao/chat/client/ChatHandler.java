package com.gupao.chat.client;

import com.gupao.chat.client.handler.ChatClientHandler;
import com.gupao.chat.protocol.IMDecoder;
import com.gupao.chat.protocol.IMEncoder;
import com.gupao.chat.server.ChatServer;
import com.gupao.chat.server.handler.HttpHandler;
import com.gupao.chat.server.handler.SocketHandler;
import com.gupao.chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatHandler {
    private ChatClientHandler chatClientHandler ;
    private int port = 8080;
    private String host;

    public ChatHandler(String nickName) {
        chatClientHandler = new ChatClientHandler(nickName);
    }

    public void connect(String host, int port){

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap server = new Bootstrap();
            server.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // =============用来支持自定义socket协议的=============
                            ch.pipeline()
                                    .addLast(new IMEncoder())
                                    .addLast(new IMDecoder())
                                    .addLast(chatClientHandler);
                        }
                    });

            ChannelFuture future = server.bind(this.port).sync();
            System.out.println("聊天客户端已经启动：" + this.port);
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new ChatHandler("").connect("127.0.0.1", 8080);
    }

}
