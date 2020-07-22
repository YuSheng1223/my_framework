package com.ly.persistence.sqlSession;

import com.ly.persistence.pojo.Configuration;
import com.ly.persistence.pojo.MappedStatement;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    private Executor executor = new SimpleExecutor();

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {

        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        //调用执行器执行sql
        List<E> list = executor.query(configuration, mappedStatement, params);

        return list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        //实际调用selectList方法
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        }
        throw new RuntimeException("返回结果过多");
    }

    /***
     * 通过反射 获取对应接口的代理对象
     * @param interfaceClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T getMapper(Class<?> interfaceClass) {

        Object proxy = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] params) throws Throwable {

                //在真正执行代理方法的位置 调用jdbc方法
                String name = method.getName();
                //全类名
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + name;
                System.out.println("  statementId 为  :  " + statementId);
                Type genericReturnType = method.getGenericReturnType();
                //返回值类型 如果属于 泛型类型参数化 说明返回的是集合
                if (genericReturnType instanceof ParameterizedType) {
                    List<Object> objects1 = selectList(statementId, params);
                    return objects1;
                }

                return selectOne(statementId, params);
            }
        });
        return (T) proxy;
    }
}
