package com.gmail.leonard.spring.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);

    public static Exception generateForStack(Thread t) {
        Exception e = new Exception("Stack Trace");
        e.setStackTrace(t.getStackTrace());
        return e;
    }



}