package com.ly.persistence.sqlSession;

import com.ly.persistence.config.BoundSql;
import com.ly.persistence.pojo.Configuration;
import com.ly.persistence.pojo.MappedStatement;
import com.ly.persistence.utils.GenericTokenParser;
import com.ly.persistence.utils.ParameterMapping;
import com.ly.persistence.utils.ParameterMappingTokenHandler;
import com.ly.persistence.utils.TokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/***
 * 真正的执行器
 */
public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //获取数据库连接
        Connection connection = configuration.getDataSource().getConnection();


        //解析sql  将 select * from t_user where id = #{id} and name = #{name}
        // 解析为 select * from t_user where id = ? and name = ?  并且将 对应参数存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        System.out.println(" 预处理之后的sql :  " + boundSql.getSqlText());
        //预编译sql
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());


        //设置参数
        //获取到参数类型 根据参数类型获取到对应的类对象
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();
            //反射
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);

            //设置参数
            preparedStatement.setObject(i + 1, o);
        }

        //执行sql
        ResultSet resultSet = preparedStatement.executeQuery();

        //获取返回值类型
        String resultType = mappedStatement.getResultType();
        Class<?> classType = getClassType(resultType);

        List<Object> objects = new ArrayList<>();
        //封装返回结果集
        while (resultSet.next()) {
            Object o = classType.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //字段名
                String columnName = metaData.getColumnName(i);

                //对应的值
                Object object = resultSet.getObject(columnName);

                //使用反射 完成数据库和实体之间的赋值
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName,classType);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,object);
            }
            objects.add(o);
        }

        return (List<E>)objects;
    }

    /***
     * 完成对sql的解析 #{} 替换为 ?  将#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();

        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", tokenHandler);

        String parse = genericTokenParser.parse(sql);

        List<ParameterMapping> parameterMappings = tokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parse, parameterMappings);

        return boundSql;
    }


    private Class<?> getClassType(String parameterType) throws Exception {
        Class<?> aClass = null;
        if (parameterType != null) {
            aClass = Class.forName(parameterType);
        }
        return aClass;
    }
}
