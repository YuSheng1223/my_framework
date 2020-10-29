package com.ly.factory;

import com.ly.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/***
 * 代理工厂
 */
public class ProxyFactory {

    private static final Logger logger = LoggerFactory.getLogger(ProxyFactory.class);

    private TransactionManager transactionManager;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    //    private static ProxyFactory proxyFactory = new ProxyFactory();
//
//    /***
//     * 获取ProxyFactory实例 ---单例
//     * @return
//     */
//    public static ProxyFactory getInstance() {
//
//        return proxyFactory;
//    }


    /***
     * 获取JDK动态代理对象
     * @param obj
     * @return
     */
    public Object getJDKProxyObject(Object obj) {

        Object o = Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                try {
                    //在代理方法内增加事务控制
                    transactionManager.beginTransaction();

                    result = method.invoke(obj, args);

                    transactionManager.commit();

                } catch (Exception e) {

                    logger.info("JDK代理对象执行对应方法发生异常 ： {}", e.toString());

                    transactionManager.rollback();

                    e.printStackTrace();
                }

                return result;

            }
        });

        return o;

    }


    /***
     * 获取cglib动态代理对象
     * @param obj
     * @return
     */
    public Object getCglibProxyObject(Object obj) {

        Object o = Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try {
                    transactionManager.beginTransaction();

                    method.invoke(obj, objects);

                    transactionManager.commit();

                } catch (Exception e) {
                    logger.info("cglib代理对象执行对应方法发生异常 ：  {} ", e.toString());

                    transactionManager.rollback();

                    e.printStackTrace();
                }
                return result;
            }
        });

        return o;
    }

}
