package com.gupao.rpc.provider;

import com.gupao.rpc.api.IRpcHello;

public class RpcHello implements IRpcHello {

    @Override
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
