package com.ly.client;

import com.ly.handler.UserClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcConsumer {


    // 创建一个线程池对象
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    private static UserClientHandler userClientHandler ;

    // 代理对象

    /***
     * 创建代理对象
     * @param serverClass
     * @param providerName 约定格式： UserService#sayHello#are you ok
     * @return
     */
    public Object createProxy(Class<?> serverClass,String providerName){
        // 借助jdk动态代理生成代理对象
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serverClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                // 在代理对象的创建、执行过程中 需要创建netty客户端已经调用远程方法

                // 初始化netty客户端
                if(Objects.isNull(userClientHandler)){
                    initNetty();
                }


                //设置参数 去服务端请求数据

                userClientHandler.setPara(providerName + args[0]);


                return executor.submit(userClientHandler).get();
            }
        });


    }

    /***
     * 初始化netty客户端
     */
    public static void initNetty() throws InterruptedException {

        userClientHandler = new UserClientHandler();

        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)

                .channel(NioSocketChannel.class)
                // 声明TCP
                .option(ChannelOption.TCP_NODELAY,true)

                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());

                        pipeline.addLast(userClientHandler);
                    }
                });


          bootstrap.connect("127.0.0.1", 8000).sync();
    }


}
