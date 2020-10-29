package com.ly.persistence.sqlSession;

import com.ly.persistence.pojo.Configuration;
import com.ly.persistence.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;
}
