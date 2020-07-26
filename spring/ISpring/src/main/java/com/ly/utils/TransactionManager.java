package com.ly.utils;

import java.sql.SQLException;

/****
 * 事务管理器工具类
 * 提供统一的事务管理方法
 */
public class TransactionManager {

    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }


    private static  TransactionManager transactionManager = new TransactionManager();

    /***
     * 获取事务管理器实例
     * @return
     */
    public static TransactionManager getInstance(){
        return transactionManager;
    }

    /***
     * 设置事务为手动提交
     * @throws SQLException
     */
    public void beginTransaction() throws SQLException {
        connectionUtils.getConnectionFromCurrentThread().setAutoCommit(false);
    }

    /***
     * 提交
     * @throws SQLException
     */
    public void commit() throws SQLException {
        connectionUtils.getConnectionFromCurrentThread().commit();
    }

    /***
     * 回滚
     * @throws SQLException
     */
    public void rollback() throws SQLException {

        connectionUtils.getConnectionFromCurrentThread().rollback();

    }
}
