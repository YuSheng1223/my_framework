package com.ly.demo.service.Impl;

import com.ly.annotation.CustomService;
import com.ly.demo.service.HelloService;

@CustomService("helloService")
public class HelloServiceImpl implements HelloService {


    @Override
    public void sayHello(String name) {
        System.out.println("hello........." + name);
    }
}
