package com.ly.framework;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ly.annotation.CustomAutowired;
import com.ly.annotation.CustomController;
import com.ly.annotation.CustomRequestMapping;
import com.ly.annotation.CustomService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


public class CustomDispatcherServlet extends HttpServlet {


    private Element rootElement = null;

    private List<String> classList = new ArrayList<>(10);

    private Map<String, Object> ioc = new HashMap<>(16);

    //private Map<String, Method> handlerMapping = new HashMap<>(16);
    private List<Handler> handlerMapping = new ArrayList<>(10);

    @Override
    public void init(ServletConfig config) throws ServletException {

        // 1. 加载配置文件 mvc.xml
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);

        // 2.扫描相关的类以及注解
        Element element = (Element) rootElement.selectNodes("//scan").get(0);

        String basePackage = element.attributeValue("base-package");

        doScan(basePackage);

        // 3. 初始化bean对象 (注册 ioc容器 基于注解的形式)

        doInstance();

        // 4. 实现依赖注入

        doAutoWired();

        // 5. 构造一个HandlerMapping处理器映射器，将配置好的url 和 method建立映射关系

        initHandlerMapping();
        //super.init(config);

        System.out.println(" mvc 加载完成.... ");
    }


    @Override
    public void destroy() {
        super.destroy();
        classList.clear();
        ioc.clear();
        handlerMapping.clear();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // super.doPost(req, resp);
        //根据req找到对应的handler
        Handler handler = getHandler(req);

        if (handler == null) {
            resp.getWriter().write(" 404 not found ");
            return;
        }

        // 找到了对应的handler  获取参数
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        Object[] paramValues = new Object[parameterTypes.length];
        //请求携带的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {

            String value = StringUtils.join(param.getValue(), ",");
            // 如果参数列表中不包含req中的参数
            if (!handler.getParamIndexMapping().containsKey(param.getKey())) {
                continue;
            }

            //包含 填充
            Integer index = handler.getParamIndexMapping().get(param.getKey());

            paramValues[index] = value;

        }

        // 填充httpServletRequest HttpServletResponse
        Integer requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());

        paramValues[requestIndex] = req;

        Integer responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());

        paramValues[responseIndex] = resp;

        try {
            handler.getMethod().invoke(handler.getController(), paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    /****
     * 根据request的url匹配对应的handler
     * @param req
     * @return
     */
    private Handler getHandler(HttpServletRequest req) {

        // 匹配url
        String url = req.getRequestURI();

        for (Handler handler : handlerMapping) {

            Matcher matcher = handler.getPattern().matcher(url);

            if (!matcher.matches()) {
                continue;
            }

            return handler;
        }

        return null;

    }

    /***
     * 读取配置文件
     * @param contextConfig
     */
    private void doLoadConfig(String contextConfig) {

        try {
            InputStream resourceAsStream = this.getClass().getResourceAsStream(contextConfig);

            SAXReader saxReader = new SAXReader();

            System.out.println(" contextConfig:  " + contextConfig + "  resourceAsStream : " + resourceAsStream);

            Document read = saxReader.read(resourceAsStream);

            rootElement = read.getRootElement();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    /***
     * 获取配置的包扫描信息
     * @param basePackage
     */
    private void doScan(String basePackage) {

        String scanPackage = Thread.currentThread().getContextClassLoader().getResource("").getPath() + basePackage.replaceAll("\\.", "/");

        File scanPackageFile = new File(scanPackage);

        File[] files = scanPackageFile.listFiles();

        for (File file : files) {

            if (file.isDirectory()) {
                //递归继续扫描
                doScan(basePackage + "." + file.getName());

            } else if (file.getName().endsWith(".class")) {

                String className = basePackage + "." + file.getName().replaceAll(".class", "");

                classList.add(className);
            }
        }

    }


    /***
     * ioc容器
     *  基于classList缓存的全限定类名 以及反射技术完成对象的创建和管理
     */
    private void doInstance() {

        try {
            for (String className : classList) {

                Class<?> aClass = Class.forName(className);

                if (aClass.isAnnotationPresent(CustomController.class)) {
                    //如果为controller 首字母大写变小写
                    String simpleName = aClass.getSimpleName();
                    String lowerFirstClassName = lowerFirst(simpleName);
                    Object o = aClass.newInstance();
                    ioc.put(lowerFirstClassName, o);
                } else if (aClass.isAnnotationPresent(CustomService.class)) {
                    // service
                    CustomService annotation = aClass.getAnnotation(CustomService.class);

                    if (!StringUtils.isEmpty(annotation.value())) {
                        // 指定了注解的value值
                        ioc.put(annotation.value(), aClass.newInstance());
                    } else {
                        // 没有指定value
                        String simpleName = aClass.getSimpleName();
                        String lowerFirstClassName = lowerFirst(simpleName);
                        //以首字母小写为key
                        ioc.put(lowerFirstClassName, aClass.newInstance());

                    }

                    // service 层往往是存在接口的 面向接口开发 此时再以接口名为id 放入一份到ioc中 方便后期根据接口类型注入

                    Class<?>[] interfaces = aClass.getInterfaces();

                    for (Class<?> anInterface : interfaces) {
                        // 以接口的全限定类名作为id放入 ????
                        ioc.put(anInterface.getName(), aClass.newInstance());
                    }

                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }

    /****
     * 为被CustomAutowired注解标记的对象注入
     */
    private void doAutoWired() {
        // 基本思想就是 遍历整个ioc，然后找到对象的对应属性 看是否被CustomAutowired注解

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            // 获取所有属性
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();

            for (Field declaredField : declaredFields) {
                //判断是否被CustomAutowired注解标识
                if (!declaredField.isAnnotationPresent(CustomAutowired.class)) {
                    continue;
                }

                CustomAutowired annotation = declaredField.getAnnotation(CustomAutowired.class);

                String value = annotation.value();

                if (StringUtils.isEmpty(value)) {
                    // 如果CustomAutowired注解中没有配置具体的值 根据类型去拿
                    value = declaredField.getType().getName();
                }
                // 开启赋值
                declaredField.setAccessible(true);

                try {
                    declaredField.set(entry.getValue(), ioc.get(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /****
     * 构建处理器映射器 建议url 和对应method对应关系
     */
    private void initHandlerMapping() {

        // 遍历ioc
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Class<?> aClass = entry.getValue().getClass();

            //判断是否为Controller
            if (!aClass.isAnnotationPresent(CustomController.class)) {
                continue;
            }

            String baseUrl = "";
            if (aClass.isAnnotationPresent(CustomRequestMapping.class)) {
                //这里假设CustomRequestMapping注解上一定存在value
                baseUrl = aClass.getAnnotation(CustomRequestMapping.class).value();
            }

            Method[] methods = aClass.getMethods();

            for (Method method : methods) {

                if (!method.isAnnotationPresent(CustomRequestMapping.class)) {
                    continue;
                }

                String url = method.getAnnotation(CustomRequestMapping.class).value();


                // 将相关信息封装为handler对象
                Handler handler = new Handler(entry.getValue(), method, Pattern.compile(baseUrl + url));

                Parameter[] parameters = method.getParameters();

                for (int i = 0; i < parameters.length; i++) {

                    Parameter parameter = parameters[i];

                    if (parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        //针对HttpServletRequest、HttpServletResponse类型
                        // 如果为这两种对象 参数名称就写为 HttpServletRequest、HttpServletResponse
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), i);
                    } else {
                        //针对String类型
                        handler.getParamIndexMapping().put(parameter.getName(), i);
                    }


                }
                handlerMapping.add(handler);

            }

        }

    }


    /****
     * 首字母小写
     * @param str
     * @return
     */
    public String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        if ('A' <= chars[0] && chars[0] <= 'Z') {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }


}
