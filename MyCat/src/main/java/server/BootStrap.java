package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class BootStrap {

    private static final Logger logger = LoggerFactory.getLogger(BootStrap.class);

    private int port = 8080;

    private Map<String, HttpServlet> servletMap = new HashMap<>(16);

    int corePoolSize = 10;
    int maximumPoolSize = 50;
    long keepAliveTime = 100L;
    TimeUnit unit = TimeUnit.SECONDS;
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {


        BootStrap bootStrap = new BootStrap();

        try {

            // 加载配置文件
            bootStrap.loadServlet();

            bootStrap.startV2();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 加载配置文件 读取servlet与url映射关系
     */
    private void loadServlet() {

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");

        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);

            Element rootElement = document.getRootElement();
            List<Node> nodes = rootElement.selectNodes("//servlet");

            Iterator<Node> iterator = nodes.iterator();

            while (iterator.hasNext()) {

                Element element = (Element) iterator.next();

                Element servletNameElement = (Element) element.selectSingleNode("servlet-name");

                String servletName = servletNameElement.getStringValue();


                Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                // servlet 全类名
                String servletClass = servletClassElement.getStringValue();
                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");


                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();

                servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());

            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    /***
     * start方法 V1
     * 请求localhost:8080 响应为一段字符串
     * @throws Exception
     */
    private void startV1() throws Exception {


        System.out.println("MyCat start......");

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {

            System.out.println("receive request...... ");

            Socket socket = serverSocket.accept();

            OutputStream stream = socket.getOutputStream();

            String response = "6666";
            // 通过工具类获取http响应格式
            String s = HttpProtocolUtil.successResponse(response.getBytes().length) + response;

            stream.write(s.getBytes());

            socket.close();


        }


    }


    /***
     * start方法 V2
     * 请求localhost:8080 接收动态请求 既可以输出html 也可以请求到servlet处理
     * @throws Exception
     */
    private void startV2() throws Exception {


        System.out.println("MyCat start......");

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {

            System.out.println("receive request...... ");

            Socket socket = serverSocket.accept();

            Request request = new Request(socket.getInputStream());

            Response response = new Response(socket.getOutputStream());

            if (servletMap.get(request.getUrl()) == null) {

                // response.output(HttpProtocolUtil.notFoundResponse());
                response.outputHtml(request.getUrl());
            } else {

                HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request, response);
            }

            socket.close();


        }


    }


    /***
     * start方法 V3
     * 请求localhost:8080 接收动态请求 既可以输出html 也可以请求到servlet处理
     * 使用单独的线程处理每个请求
     * @throws Exception
     */
    private void startV3() throws Exception {


        System.out.println("MyCat start......");

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {

            System.out.println("receive request...... ");

            Socket socket = serverSocket.accept();
//            // 不使用线程池的方式
//            RequestProcessor requestProcessor = new RequestProcessor(socket,servletMap);
//
//            requestProcessor.run();
            RequestProcessor requestProcessor = new RequestProcessor(socket, servletMap);

            threadPoolExecutor.execute(requestProcessor);

        }


    }
}
