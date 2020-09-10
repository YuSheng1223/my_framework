package server;

import java.io.IOException;

public class CustomServlet extends HttpServlet {

    @Override
    public void doGet(Request request, Response response) {
        System.out.println(" receive get method request...... ");
        String data = "<h1>GET METHOD!</h1>";

        try {
            response.output(HttpProtocolUtil.successResponse(data.getBytes().length) + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        System.out.println(" receive post method request...... ");
        String data = "<h1>POST METHOD!</h1>";

        try {
            response.output(HttpProtocolUtil.successResponse(data.getBytes().length) + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void destory() {

    }
}
