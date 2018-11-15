package com.gupao.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义IM协议的编码器
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {

    }

    public String encode(IMMessage msg){
        if(null == msg){
            return "";
        }
        String prex = "[" + msg.getCmd() + "]" + "[" + msg.getTime() + "]";
        if(IMP.LOGIN.getName().equals(msg.getCmd())
                || IMP.CHAT.equals(msg.getCmd())
                || IMP.FLOWER.equals(msg.getCmd())){
            prex += ("[" + msg.getSender() + "]");
        }else if(IMP.SYSTEM.getName().equals(msg.getCmd())){
            prex += ("[" + msg.getOnline() + "]");
        }
        if(null != msg.getContent() && !"".equals(msg.getContent())){
            prex += (" - " + msg.getContent());
        }
        return prex;
    }

}
