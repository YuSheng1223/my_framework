package com.ly.dao.impl;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.ly.bean.User;
import com.ly.dao.UserDao;
import com.ly.utils.ConnectionUtils;
import com.ly.utils.DruidUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDaoImpl implements UserDao {

    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    @Override
    public User gerUserById(String id) throws Exception {


        String sql = "select * from t_user where id = ?";
        //DruidPooledConnection connection = DruidUtils.getInstance().getConnection();
        Connection connection = connectionUtils.getConnectionFromCurrentThread();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, 1);
        ResultSet resultSet = preparedStatement.executeQuery();

        User user = new User();
        while (resultSet.next()) {

            user.setId(Integer.valueOf(resultSet.getString("id")));
            user.setName(resultSet.getString("name"));
        }

        return user;
    }

    @Override
    public User update(User user) throws Exception {

        String sql = "update t_user set name = ? where id = ?";
        Connection connection = connectionUtils.getConnectionFromCurrentThread();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, user.getName());
        preparedStatement.setObject(2, user.getId());

        int i = preparedStatement.executeUpdate();

        return user;
    }
}
