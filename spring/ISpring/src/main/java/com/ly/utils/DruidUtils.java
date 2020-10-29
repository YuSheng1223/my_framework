package com.ly.utils;

import com.alibaba.druid.pool.DruidDataSource;

/***
 * 数据库连接池druid工具类
 */
public class DruidUtils {

    private DruidUtils() {
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/zdy_mybatis?serverTimezone=UTC");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");

    }

    /***
     * 获取druid数据库连接池实例  ----单例
     * @return
     */
    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
