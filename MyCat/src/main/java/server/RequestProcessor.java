package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/***
 * 请求处理线程
 */
public class RequestProcessor extends Thread {


    private Socket socket;

    private Map<String, HttpServlet> servletMap;


    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }

    @Override
    public void run() {

        try {

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

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
