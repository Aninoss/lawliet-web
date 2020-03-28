package com.gmail.leonard.spring;

import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.vaadin.flow.component.UI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws FileNotFoundException {
        SpringApplication.run(Application.class, args);
        System.out.println("###########################");
        Console.getInstance().start();
        WebComClient.getInstance().start(15744);
    }

}