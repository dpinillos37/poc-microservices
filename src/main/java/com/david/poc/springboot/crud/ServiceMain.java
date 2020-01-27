package com.david.poc.springboot.crud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.david.poc.springboot.crud")
@MapperScan("com.david.poc.springboot.crud.dao")
@EnableAutoConfiguration
public class ServiceMain extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(ServiceMain.class, args);
  }
}
