package com.ly.service;

import com.ly.handler.UserServerHandler;
import com.ly.server.UserService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class UserServiceImpl implements UserService {

    @Override
    public String sayHello(String word) {

        System.out.println("调用远程方法成功----参数： " + word);

        return "调用UserServiceImpl成功----参数： " + word;
    }

    /***
     *
     * @param hostName ip
     * @param port 端口
     */
    public static void startServer(String hostName ,int port) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workerGroup)

                .channel(NioServerSocketChannel.class)

                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new StringEncoder());

                        pipeline.addLast(new StringDecoder());
                        // 自己的逻辑处理 自定义类
                        pipeline.addLast( new UserServerHandler());
                    }
                });

        ChannelFuture sync = serverBootstrap.bind(hostName, port).sync();

    }
}
