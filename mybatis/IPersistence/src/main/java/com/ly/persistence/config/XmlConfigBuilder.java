package com.ly.persistence.config;

import com.ly.persistence.io.Resources;
import com.ly.persistence.pojo.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.convert.Property;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/***
 * 解析configxml
 */
public class XmlConfigBuilder {

    private Configuration configuration;

    public XmlConfigBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    /***
     * 解析xml文件
     * @param inputStream
     * @return
     * @throws DocumentException
     * @throws PropertyVetoException
     */
    public Configuration parseConfig(InputStream inputStream)throws DocumentException , PropertyVetoException {

        //解析sqlMapConfig.xml
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(inputStream);

        Element rootElement = read.getRootElement();

        List<Node> list = rootElement.selectNodes("//property");

        Properties properties = new Properties();

        for (Node element : list) {
            String name = ((Element)element).attributeValue("name");

            String value = ((Element)element).attributeValue("value");

            properties.setProperty(name,value);
        }

        //连接池
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));

        configuration.setDataSource(comboPooledDataSource);


        //解析mapper.xml
        List<Node> mapperList = rootElement.selectNodes("//mapper");

        for (Node element : mapperList) {
            String resource = ((Element)element).attributeValue("resource");
            InputStream resourceAsStream = Resources.getResourceAsStream(resource);
            XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder(configuration);
            xmlMapperBuilder.parse(resourceAsStream);
        }
        return configuration;
    }
}
