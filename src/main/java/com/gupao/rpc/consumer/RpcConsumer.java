package com.gupao.rpc.consumer;

import com.gupao.rpc.api.IRpcHello;
import com.gupao.rpc.consumer.proxy.RpcProxy;

public class RpcConsumer {

    public static void main(String[] args) {

        IRpcHello rpcHello = RpcProxy.create(IRpcHello.class);
        String hello = rpcHello.hello("小王八蛋");
        System.out.println(hello);

    }

}
