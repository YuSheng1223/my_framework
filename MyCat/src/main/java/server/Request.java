package server;

import java.io.InputStream;

/***
 * 接受http请求
 */
public class Request {

    /***
     * 请求方式
     */
    private String method;
    /***
     * 请求url
     */
    private String url;
    /***
     * 输入流
     */
    private InputStream inputStream;


    public Request() {
    }

    public Request(InputStream inputStream) throws Exception {

        this.inputStream = inputStream;

        // 从输入流中获取请求信息
        // 避免请求到了 数据没到
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }

        byte[] bytes = new byte[count];

        inputStream.read(bytes);

        String inputString = new String(bytes);

        // 获取第一行请求头信息
        String firstLineStr = inputString.split("\\n")[0];  // GET / HTTP/1.1

        String[] strings = firstLineStr.split(" ");

        this.method = strings[0];
        this.url = strings[1];

        System.out.println("=====>>method:" + method);
        System.out.println("=====>>url:" + url);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
