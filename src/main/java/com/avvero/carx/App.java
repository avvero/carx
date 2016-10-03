package com.avvero.carx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Avvero
 */
@ComponentScan
@EnableAutoConfiguration
@EnableJpaRepositories
public class App {

    public static void main(String args[]) throws Throwable {
        SpringApplication.run(App.class, args);
    }

}
