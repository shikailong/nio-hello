package com.gupao.chat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

//    private final URL baseURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
    private final String baseURL = System.getProperty("user.dir") + File.separator +
                            "src" + File.separator + "main" ;
    private final String WEB_ROOT = "webroot";

    private File getFileFromRoot(String fileName){
        String path = baseURL + File.separator + "resources" + File.separator + "webroot" + File.separator + fileName;
        return new File(path);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        String page = uri.equals("/") ? "chat.html" : uri;

        RandomAccessFile file = new RandomAccessFile(getFileFromRoot(page), "r");
        String contextType = "text/html;";
        if(uri.endsWith(".css")){
            contextType = "text/css;";
        }else if(uri.endsWith(".js")){
            contextType = "text/javascript;";
        }else if(uri.toUpperCase().matches("(jpg|png|gif|ico)$")){
            String ext = uri.substring(uri.lastIndexOf("."));
            contextType = "image/" + ext + ";";
        }

        HttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contextType + "charset=utf-8;");
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if(keepAlive){
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));

        // 清空缓冲区
        ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!keepAlive){
            f.addListener(ChannelFutureListener.CLOSE);
        }
        file.close();
        System.out.println(file);
    }

}
