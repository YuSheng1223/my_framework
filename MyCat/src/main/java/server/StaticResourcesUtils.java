package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/****
 * 静态资源工具类
 */
public class StaticResourcesUtils {


    /***
     * 获取静态资源文件的绝对路径
     * @param path
     * @return
     */
    public static String getAbsolutePath(String path) {
        String absolutePath = StaticResourcesUtils.class.getResource("/").getPath();
        return absolutePath.replaceAll("\\\\", "/") + path;
    }


    public static void outputStaticResource(InputStream fileInputStream, OutputStream outputStream) throws IOException {

        int count = 0;
        while (count == 0) {
            count = fileInputStream.available();
        }
        // 静态资源总的大小
        int resourceSize = count;
        // 先输出请求头
        outputStream.write(HttpProtocolUtil.successResponse(resourceSize).getBytes());

        // 读取内容输出

        // 已经写入大小
        long written = 0;

        // 计划每次缓冲大小
        int byteSize = 1024;

        // 缓冲数组
        byte bytes[] = new byte[byteSize];

        // 避免文件过大分批次写出
        while (written < resourceSize) {

            if (written + byteSize > resourceSize) {
                byteSize = (int) (resourceSize - written);
                bytes = new byte[byteSize];

            }

            fileInputStream.read(bytes);

            outputStream.write(bytes);

            outputStream.flush();

            written+=byteSize;


        }

    }
}
