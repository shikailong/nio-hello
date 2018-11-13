package com.gupao.netty.server.http;

public abstract class GpServlet {

    public abstract void doGet(GpRequest req, GpResponse resp);
    public abstract void doPost(GpRequest req, GpResponse resp);

}
