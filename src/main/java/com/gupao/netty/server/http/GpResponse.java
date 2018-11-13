package com.gupao.netty.server.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;


public class GpResponse {

    private ChannelHandlerContext ctx;
    private HttpRequest r;


    public GpResponse(ChannelHandlerContext ctx, Object r) {
        this.ctx = ctx;
        this.r = (HttpRequest)r;
    }


    public void write(String out) throws UnsupportedEncodingException {
        try {
            if(out == null) return ;
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer(out.getBytes("utf-8")));

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.EXPIRES, -1);
            if(HttpUtil.isKeepAlive(r)){
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);
        } finally {
            ctx.flush();
        }

    }

}
