package com.ly.export;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author xes
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
