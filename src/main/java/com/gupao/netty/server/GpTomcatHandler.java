package com.gupao.netty.server;

import com.gupao.netty.server.http.GpRequest;
import com.gupao.netty.server.http.GpResponse;
import com.gupao.netty.server.servlets.MyServlet;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

public class GpTomcatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            HttpRequest r = (HttpRequest)msg;
            GpRequest request = new GpRequest(ctx, msg);
            GpResponse response = new GpResponse(ctx, msg);
            MyServlet servlet = new MyServlet();
            servlet.doGet(request, response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
