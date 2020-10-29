package com.ly.factory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/***
 * bean工厂
 * 负责加载配置文件中配置的bean到容器中以及提供外部可获取bean的方法
 */
public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private static Map<String, Object> beanMap = new HashMap<>();

    /***
     * 初始化beanFactory
     * 将配置文件中的bean加载到容器中
     */
    public static void initBeanFactory() {


        try {
            //1.加载配置文件为流
            InputStream resourceAsStream = BeanFactory.class.getResourceAsStream("/beans.xml");

            //2.读取xml文件
            SAXReader saxReader = new SAXReader();

            Document read = saxReader.read(resourceAsStream);

            Element rootElement = read.getRootElement();
            //3.加载bean标签
            List<Node> nodes = rootElement.selectNodes("//bean");

            Iterator<Node> iterator = nodes.iterator();

            while (iterator.hasNext()) {
                //4.解析出bean标签对应的id和class属性
                Element element = (Element) iterator.next();
                String id = element.attributeValue("id");
                String className = element.attributeValue("class");

                logger.info(" 从xml 中加载出来的标签信息   id ： {} ， class : {}", id, className);

                //5.通过反射创建实例对象 放入beanMap中

                Class<?> beanClass = Class.forName(className);

                Object instance = beanClass.newInstance();

                beanMap.put(id, instance);

            }


            //如果存在对象依赖对象的情况
            //读取property属性
            List<Node> propertyNode = rootElement.selectNodes("//property");
            Iterator<Node> iterator1 = propertyNode.iterator();
            while (iterator1.hasNext()) {

                Element element = (Element) iterator1.next();
                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");

                //根据ref属性从map中获取对应的实例
                Object refObject = beanMap.get(ref);

                //获取父节点的class
                Element parent = element.getParent();
                String parentId = parent.attributeValue("id");
                Object parentObject = beanMap.get(parentId);
                //父对象 获取父对象set方法
                Method[] methods = parentObject.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    //找到对应的set方法
                    if (("set" + name).equals(methods[i].getName())) {
                        methods[i].invoke(parentObject, refObject);
                    }
                }

                //维护完依赖关系重新放入map
                beanMap.put(parentId, parentObject);
            }


            logger.info(" 完成beans.xml解析，并且全部配置对象加载完毕..... ");
            for (String key : beanMap.keySet()) {
                logger.info("beanMap ----> key : {} ,value  : {}", key, beanMap.get(key));
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取到容器中的bean实例
     * @param className
     * @return
     */
    public static Object getBeanByName(String className) {

        Object o = beanMap.get(className);
        if (o != null) {
            return o;
        }
        logger.error(" 未能从获取到对应的对象实例  ，className :  {}", className);
        return null;
    }
}
