package com.gupao.rpc.registry;

import com.gupao.rpc.core.msg.InvokerMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理整个注册中心的业务逻辑
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    // 在注册中心注册的服务，需要有一个容器存放
    public static Map<String, Object> registryMap = new ConcurrentHashMap<>();
    // 约定，只要写在provider包下面的所有类都认为是一个可以对外部提共服务的实现类
    // com.gupao.rpc.provider

    public List<String> classCache = new ArrayList<>();

    public RegistryHandler() {
        System.out.println("初始化handler完成");
        scanClass("com.gupao.rpc.provider");
        doRegistry();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Object result = new Object();

        // 客户端传过来的调用信息
        InvokerMsg request = (InvokerMsg) msg;
        if(registryMap.containsKey(request.getClassName())){
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValues());

        }
        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    public void scanClass(String packageName){
        URL url = RegistryHandler.class.getClassLoader().getResource(packageName.replace("\\.", "/"));
        File dir = new File(url.getFile());

        for (File file : dir.listFiles()){
            if(file.isDirectory()){
                scanClass(packageName + "." + file.getName());
            }else{
                classCache.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }

    }

    private void doRegistry(){
        if(classCache.size() == 0){
            return ;
        }

        try {
            for(String className : classCache){
                Class<?> aClass = Class.forName(className);
                Class<?> interfaces = aClass.getInterfaces()[0];

                registryMap.put(interfaces.getName(), aClass.newInstance());

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


//    public static void main(String[] args) {
//        Class<?>[] interfaces = RegistryHandler.class.getInterfaces();
//
//
//
//        System.out.println(interfaces[0].getName());
//        System.out.println(RegistryHandler.class.getGenericSuperclass()[0].getTypeName());
//        RegistryHandler.class.getAnnotatedInterfaces()[0].getType().getTypeName();
//    }

}
