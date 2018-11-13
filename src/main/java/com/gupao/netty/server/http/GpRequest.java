package com.gupao.netty.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class GpRequest {

    private ChannelHandlerContext ctx;
    private HttpRequest r;

    public GpRequest(ChannelHandlerContext ctx, Object r) {
        this.ctx = ctx;
        this.r = (HttpRequest)r;
    }

    public String getUri(){
        return r.uri();
    }

    public String getMethod(){
        return r.method().name();
    }

    public Map<String, List<String>> getParammeters(){
        QueryStringDecoder decoder = new QueryStringDecoder(r.uri());
        return decoder.parameters();
    }

    public String getParammeter(String name){
        Map<String, List<String>> parammeters = getParammeters();
        List<String> params = parammeters.get(name);
        if(null == params){
            return null;
        }else{
            return params.get(0);
        }
    }

}
