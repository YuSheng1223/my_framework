package com.ly.servlet;

import com.ly.bean.User;
import com.ly.factory.BeanFactory;
import com.ly.factory.ProxyFactory;
import com.ly.service.UserService;
import com.ly.service.impl.UserServiceImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@javax.servlet.annotation.WebServlet(name="webServlet",urlPatterns = "/webServlet")
public class WebServlet extends HttpServlet {


    static{
        BeanFactory.initBeanFactory();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("  Post请求............ ");


        try {
            /***
             * UserService userService = new UserServiceImpl();
             * 已上述方式创建对象 存在硬编码问题 如果实现类变更新 则需要修改所有的创建对象代码
             */
//            UserService userService = new UserServiceImpl();
            ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBeanByName("proxyFactory");

            UserService userService = (UserService) proxyFactory.getJDKProxyObject(BeanFactory.getBeanByName("userService"));

            User user = userService.gerUserById("1");

            System.out.println("查询出来的结果  ：  " + user.toString());

        } catch (Exception e) {
            e.printStackTrace();

        }

        // 设置请求体的字符编码
        req.setCharacterEncoding("UTF-8");
        // 响应
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().print("200");
    }
}

