package com.ly.dao;

import com.ly.bean.User;

public interface UserDao {

    User gerUserById(String id)throws Exception;


    User update(User user)throws Exception;
}
