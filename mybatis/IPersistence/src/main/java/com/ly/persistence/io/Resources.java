package com.ly.persistence.io;

import java.io.InputStream;

public class Resources {

    /***
     * 将资源加载为inputStream
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(String path){
        InputStream resourceAsStream = Resources.class.getResourceAsStream(path);
        return resourceAsStream;
     //   return Resources.class.getClassLoader().getResourceAsStream(path);
    }
}
