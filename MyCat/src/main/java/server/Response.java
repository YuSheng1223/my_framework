package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/***
 * 响应
 */
public class Response {

    private OutputStream outputStream;

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void output(String content) throws IOException {
        this.outputStream.write(content.getBytes());
    }

    /***
     * 输出静态资源
     * @param path
     * @throws IOException
     */
    public void outputHtml(String path) throws IOException {
        // 绝对路径
        String absoluteResourcePath = StaticResourcesUtils.getAbsolutePath(path);

        File file = new File(absoluteResourcePath);

        if (file.exists()) {
            // 读取静态资源 输出静态资源
            StaticResourcesUtils.outputStaticResource(new FileInputStream(file), this.outputStream);
        } else {
            // 404
            output(HttpProtocolUtil.notFoundResponse());
        }

    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
