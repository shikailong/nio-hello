package com.gupao.chat.process;

import com.gupao.chat.protocol.IMDecoder;
import com.gupao.chat.protocol.IMEncoder;
import com.gupao.chat.protocol.IMMessage;
import com.gupao.chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class IMProcessor {

    private final static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private IMDecoder imDecoder = new IMDecoder();
    private IMEncoder imEncoder = new IMEncoder();

    public void logout(Channel client){
        onlineUsers.remove(client);
    }

    public void process(Channel client, String msg){

        IMMessage request = imDecoder.decode(msg);
        if(request == null){
            return ;
        }
        String sender = request.getSender();

        // 登录动作,就往onlineUsers中加入一套信息
        if(IMP.LOGIN.getName().equals(request.getCmd())){
            onlineUsers.add(client);

            // 循环ChannelGroup，通知所有的客户端
            for(Channel channel : onlineUsers){
                if(channel != client){
                    request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), sender);
                }else{
                    request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), "已与服务建立连接");
                }
                String text = imEncoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(text));
            }

        }else if(IMP.LOGOUT.getName().equals(request.getCmd())){
            onlineUsers.remove(client);
        }

    }

    private long sysTime(){
        return System.currentTimeMillis();
    }

}
