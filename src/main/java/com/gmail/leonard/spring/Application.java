package com.gmail.leonard.spring;

import com.gmail.leonard.spring.Backend.WebComClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws FileNotFoundException {
        //Redirect error outputs to a file
        String fileName = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss").format(new Date());
        File file = new File("error_log/" + fileName + "_err.log");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setErr(ps);

        SpringApplication.run(Application.class, args);
        System.out.println("###########################");
        Console.getInstance().start();
        WebComClient.getInstance().start(15744);
    }

}