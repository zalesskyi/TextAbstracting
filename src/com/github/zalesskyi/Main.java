package com.github.zalesskyi;

import com.github.zalesskyi.base.summarization.Dictionary;
import com.github.zalesskyi.base.summarization.FullText;
import com.github.zalesskyi.base.summarization.Word;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                new ProcessThread(serverSocket.accept());
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
