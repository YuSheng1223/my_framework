package com.ly.persistence.test;

import com.ly.persistence.bean.User;
import com.ly.persistence.dao.UserMapper;
import com.ly.persistence.io.Resources;
import com.ly.persistence.sqlSession.SqlSession;
import com.ly.persistence.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class PersistenceTest {

    public static void main(String[] args) {

        try {
            //springBoot
            //path 不以’/’开头时默认是从此类所在的包下取资源，以’/’开头则是从 ClassPath根下获取。其只是通过path构造一个绝对路径，最终是由ClassLoader获取资源
            String configPath = "/sqlMapConfig.xml";

            InputStream resourceAsStream = Resources.getResourceAsStream(configPath);

            SqlSession sqlSession = new SqlSessionFactoryBuilder().build(resourceAsStream).openSession();

            User user = new User();
            user.setId(2);
            user.setName("宋书航");
            List<User> objects = sqlSession.selectList("User.selectOne", user);

            for (User object : objects) {
                System.out.println(object.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 优化  使用代理对象
     */
    @Test
    public void test() {
        try {
            //springBoot
            //path 不以’/’开头时默认是从此类所在的包下取资源，以’/’开头则是从 ClassPath根下获取。其只是通过path构造一个绝对路径，最终是由ClassLoader获取资源
            String configPath = "/sqlMapConfig.xml";

            InputStream resourceAsStream = Resources.getResourceAsStream(configPath);

            SqlSession sqlSession = new SqlSessionFactoryBuilder().build(resourceAsStream).openSession();

            User user = new User();
            user.setId(1);
            user.setName("宋书航");

            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//            List<User> users = userMapper.selectList(user);
//            for (User object : users) {
//                System.out.println(object.toString());
//            }
            User user1 = userMapper.selectOne(user);
            System.out.println(user1.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
