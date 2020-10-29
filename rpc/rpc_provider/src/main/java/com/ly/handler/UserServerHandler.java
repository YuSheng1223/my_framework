package com.ly.handler;

import com.ly.service.UserServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class UserServerHandler extends ChannelInboundHandlerAdapter {

    /***
     * 重写方法 自定义逻辑处理
     * @param ctx
     * @param msg 客户端消息    约定格式： UserService#sayHello#are you ok
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断是否符合约定 符合则调用本地方法 返回数据

        if(msg.toString().startsWith("UserService")){
            UserServiceImpl userService = new UserServiceImpl();
            String result = userService.sayHello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            // 拿到方法返回结果 响应
            ctx.writeAndFlush(result);
        }else{
            System.out.println(" 不符合约定协议的请求 ......");
        }
    }
}
