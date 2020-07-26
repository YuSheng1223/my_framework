package com.ly.service.impl;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.ly.bean.User;
import com.ly.dao.UserDao;
import com.ly.service.UserService;


public class UserServiceImpl implements UserService {


    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User gerUserById(String id) throws Exception {

        //UserDao userDao = new UserDaoImpl();

       // UserDao userDao = (UserDao) BeanFactory.getBeanByName("userDao");
     //   TransactionManager.getInstance().beginTransaction();
        User user = null;
        //try{

            user = userDao.gerUserById(id);

            User user1 = new User();

            user1.setId(1);

            user1.setName("宋书航111");

            userDao.update(user1);

            //int i = 10/0;

            User user2 = new User();

            user2.setId(2);

            user2.setName("苏阿十六2222");

            userDao.update(user2);

        //    TransactionManager.getInstance().commit();

        //}catch (Exception e){
         //   e.printStackTrace();
        //    TransactionManager.getInstance().rollback();
        //}

        return user;
    }
}
