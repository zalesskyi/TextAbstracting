package com.github.zalesskyi;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(Utils.Constants.PORT), 0);
            Utils.log("Server started at port: " + Utils.Constants.PORT);
            server.createContext("/getAbstract", new GetAbstractHttpHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException exc) {
            Utils.errLog(exc.getMessage());
        }
    }
}
