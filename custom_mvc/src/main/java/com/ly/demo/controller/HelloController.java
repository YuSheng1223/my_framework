package com.ly.demo.controller;


import com.ly.annotation.CustomAutowired;
import com.ly.annotation.CustomController;
import com.ly.annotation.CustomRequestMapping;
import com.ly.demo.service.HelloService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CustomController
@CustomRequestMapping("/helloController")
public class HelloController {

    @CustomAutowired
    private HelloService HelloService;

    @CustomRequestMapping("/hello")
    public void SayHello(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse ,String name){

        HelloService.sayHello(name);
    }
}
