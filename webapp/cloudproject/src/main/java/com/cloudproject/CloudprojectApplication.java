package com.cloudproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CloudprojectApplication extends SpringBootServletInitializer {

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(CloudprojectApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CloudprojectApplication.class, args);
        System.out.print("----------------------------------------"+System.getenv("SPRING_DATASOURCE_URL")+"-----------");
    }
}
