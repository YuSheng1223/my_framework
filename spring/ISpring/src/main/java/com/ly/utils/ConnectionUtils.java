package com.ly.utils;

import java.sql.Connection;
import java.sql.SQLException;

/***
 * 数据库连接工具类 保证每一个线程只持有一个connection
 * 只有保证执行方法在同一个连接 才可能保证使用的是同一个事务
 */
public class ConnectionUtils {



    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /****
     * 从ThreadLocal中获取数据库连接 保证同一线程使用同一个数据库连接
     * @return
     * @throws SQLException
     */
    public Connection getConnectionFromCurrentThread() throws SQLException {

        Connection connection = threadLocal.get();
        if (connection == null) {

            connection = DruidUtils.getInstance().getConnection();

            threadLocal.set(connection);
        }

        return connection;
    }

}
