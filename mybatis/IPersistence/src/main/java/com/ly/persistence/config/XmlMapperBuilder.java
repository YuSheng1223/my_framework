package com.ly.persistence.config;

import com.ly.persistence.pojo.Configuration;
import com.ly.persistence.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/***
 * 解析mapper
 */
public class XmlMapperBuilder {

    private Configuration configuration;

    public XmlMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }


    public void parse(InputStream inputStream)throws DocumentException {

        Document document = new SAXReader().read(inputStream);

        Element rootElement = document.getRootElement();

        String namespace = rootElement.attributeValue("namespace");

        List<Node> list = rootElement.selectNodes("//select");

        for (Node element : list) {

            String id = ((Element)element).attributeValue("id");
            String parameterType = ((Element)element).attributeValue("parameterType");
            String resultType = ((Element)element).attributeValue("resultType");
            String sqlText = ((Element)element).getTextTrim();
            //将每一个select语句封装为MappedStatement对象
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sqlText);
            String key = namespace+ "." + id;
            //将namespace.id作为id sql的唯一标识
            configuration.getMappedStatementMap().put(key,mappedStatement);

        }

    }
}
