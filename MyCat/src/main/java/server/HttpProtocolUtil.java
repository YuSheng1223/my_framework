package server;

/***
 * Http 协议工具类
 */
public class HttpProtocolUtil {

    /***
     * 响应成功
     * @param contentLength
     * @return
     */
    public static String successResponse(long contentLength){

        String response = "HTTP/1.1 200 OK \n" +
                            "Content-Type: text/html \n" +
                            "Content-Length: "+ contentLength + "\n" + "\r\n";

        return response;


    }

    /***
     * 404
     * @param
     * @return
     */
    public static String notFoundResponse(){
        String data = "404 NOT Found";
        String response = "HTTP/1.1 404 NOT Found \n" +
                "Content-Type: text/html \n" +
                "Content-Length: "+ data.getBytes().length + "\n" + "\r\n" + data;

        return response;

    }


}
