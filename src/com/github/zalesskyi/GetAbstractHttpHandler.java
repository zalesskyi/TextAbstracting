package com.github.zalesskyi;

import com.github.zalesskyi.model.Request;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

/**
 * Created by Алексей on 28.05.2018.
 */
public class GetAbstractHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody(), "utf-8"));
        String query = reader.readLine();
        Utils.log(Thread.currentThread().getName());

        Gson gson = new Gson();
        Request req = gson.fromJson(query, Request.class);
        Utils.log(req.toString());

        new ProcessThread(req.getSource(), httpExchange);
    }
}
