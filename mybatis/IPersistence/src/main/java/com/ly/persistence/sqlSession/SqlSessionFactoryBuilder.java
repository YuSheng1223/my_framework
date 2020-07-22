package com.ly.persistence.sqlSession;

import com.ly.persistence.config.XmlConfigBuilder;
import com.ly.persistence.pojo.Configuration;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    private Configuration configuration;

    public SqlSessionFactoryBuilder() {
        this.configuration = new Configuration();
    }



    public SqlSessionFactory build(InputStream inputStream)throws  Exception{
        //根据inputStream 构建Configuration对象
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(configuration);
        Configuration configuration = xmlConfigBuilder.parseConfig(inputStream);

        //构造SqlSessionFactory
        return new DefaultSqlSessionFactory(configuration);
    }
}
