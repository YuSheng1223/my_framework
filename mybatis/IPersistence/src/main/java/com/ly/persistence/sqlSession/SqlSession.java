package com.ly.persistence.sqlSession;

import java.util.List;

public interface SqlSession {


    public <E> List<E> selectList(String statementId,Object...params)throws  Exception;

    public <T> T selectOne(String statementId,Object...params)throws  Exception;

    public <T> T getMapper(Class<?> interfaceClass);
}
