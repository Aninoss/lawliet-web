package com.gmail.leonard.spring;

import com.gmail.leonard.spring.backend.webcomclient.WebComClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        if (args.length == 0) args = new String[]{ "15744" };

        SpringApplication.run(Application.class, args);
        LOGGER.info("###########################");
        Console.getInstance().start();
        WebComClient.getInstance().start(Integer.parseInt(args[0]));
    }

}