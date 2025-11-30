package com.zr.springdebug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SpringDebugTest {

    public static void main(String[] args) {
//        ConfigurableApplicationContext context = new SpringApplicationBuilder(SpringDebugTest.class).allowCircularReferences(true).run(args);
//        context.getBean("beanA");

        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("configTest", ConfigTest.class);
        context.refresh();
    }
}

@Configuration
class ConfigTest {

    @Bean
    BeanA beanA() {
        return new BeanA();
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


