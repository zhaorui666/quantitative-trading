package com.zr.springdebug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SpringDebugTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SpringDebugTest.class).allowCircularReferences(true).run(args);
        context.getBean("beanA");
    }
}

@Component
class BeanA {

    @Autowired
    BeanB beanB;
}

@Component
class BeanB {

    @Autowired
    BeanA beanA;
}


