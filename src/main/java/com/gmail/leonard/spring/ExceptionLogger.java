package com.gmail.leonard.spring;

import com.gmail.leonard.spring.backend.commandlist.CommandListCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.function.Function;

public class ExceptionLogger {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionLogger.class);

    public static <T> Function<Throwable, T> get() {
        return throwable -> {
            LOGGER.error("Uncaught exception", throwable);
            return null;
        };
    }

}
