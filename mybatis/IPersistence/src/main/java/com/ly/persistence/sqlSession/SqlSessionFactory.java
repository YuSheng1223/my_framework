package com.ly.persistence.sqlSession;

import com.ly.persistence.pojo.Configuration;

public interface SqlSessionFactory {


    SqlSession openSession();
}
