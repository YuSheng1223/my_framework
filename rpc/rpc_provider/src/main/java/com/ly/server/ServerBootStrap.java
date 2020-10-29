package com.ly.server;

import com.ly.service.UserServiceImpl;

public class ServerBootStrap {


    public static void main(String[] args) {

        try {



            UserServiceImpl.startServer("127.0.0.1",8000);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
