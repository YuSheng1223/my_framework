package server;

/***
 * servlet接口
 */
public interface Servlet {

    void init();

    void destory();

    void service(Request request, Response response) throws Exception;
}
