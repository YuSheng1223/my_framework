package com.ly.persistence.dao;

import com.ly.persistence.bean.User;

import java.util.List;

public interface UserMapper {

    List<User> selectList(User user);

    User selectOne(User user);
}
