package com.gupao.chat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

//    private final URL baseURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
    private final String baseURL = System.getProperty("user.dir") + File.separator +
                            "src" + File.separator + "main" ;
    private final String WEB_ROOT = "webroot";

    private File getFileFromRoot(String fileName){
        String path = baseURL + File.separator + "resources" + File.separator + WEB_ROOT + File.separator + fileName;
        return new File(path);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        String page = uri.equals("/") ? "chat.html" : uri;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(getFileFromRoot(page), "r");
        } catch (FileNotFoundException e) {
            ctx.fireChannelRead(request.retain());
            e.printStackTrace();
            return ;
        }
        String contentType = "text/html;";
        if(uri.endsWith(".css")){
            contentType = "text/css;";
        }else if(uri.endsWith(".js")){
            contentType = "text/javascript;";
        }else if(uri.toUpperCase().matches("(jpg|png|gif|ico)$")){
            String ext = uri.substring(uri.lastIndexOf("."));
            contentType = "image/" + ext + ";";
        }

        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType + "charset=UTF-8");
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if(keepAlive){
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()), ctx.newProgressivePromise());
        System.out.println(file.readLine());
        System.out.println(file);
        // 清空缓冲区
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!keepAlive){
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }

        file.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught......");
        System.out.println("exceptionCaught......");
    }



}
