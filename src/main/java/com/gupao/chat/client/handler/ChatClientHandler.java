package com.gupao.chat.client.handler;

import com.gupao.chat.protocol.IMMessage;
import com.gupao.chat.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.security.cert.X509Certificate;
import java.util.Scanner;


public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger LOG = Logger.getLogger(ChatClientHandler.class);
    private ChannelHandlerContext ctx;
    private String nickName;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    private void session(){
        new Thread(()->{
            LOG.info(nickName + "，你好，请在控制台输入消息内容");
            IMMessage message = null;
            Scanner scanner = new Scanner(System.in);
            do {
                if(scanner.hasNext()){
                    String input = scanner.nextLine();
                    if("exit".equals(input)){
                        message = new IMMessage(IMP.LOGOUT.getName(), System.currentTimeMillis(), nickName);
                    }else{
                        message = new IMMessage(IMP.CHAT.getName(), System.currentTimeMillis(), nickName, input);
                    }
                }
            }while (sendMsg(message));
            scanner.close();
        });
    }

    private boolean sendMsg(IMMessage message) {
        ctx.channel().writeAndFlush(message);
        LOG.info("已发送至聊天面板，请继续输入");
        return message.getCmd().equals(IMP.LOGOUT) ? false : true ;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        IMMessage message = new IMMessage(IMP.LOGIN.getName(), System.currentTimeMillis(), nickName);
        sendMsg(message);
        LOG.info("成功连接到服务器，已执行登录操作");
        session();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMMessage m = (IMMessage)msg;
        LOG.info(m);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.info("与服务器断开连接" + cause.getMessage());
        ctx.close();
    }
}
