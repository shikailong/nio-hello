package com.gupao.netty.server.servlets;

import com.gupao.netty.server.http.GpRequest;
import com.gupao.netty.server.http.GpResponse;
import com.gupao.netty.server.http.GpServlet;

import java.io.UnsupportedEncodingException;

public class MyServlet extends GpServlet {

    @Override
    public void doGet(GpRequest req, GpResponse resp) {
        try {
            resp.write(req.getParammeter("name"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(GpRequest req, GpResponse resp) {
        doGet(req, resp);
    }
}
