package com.zr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.function.Predicate;

@Configuration
public class ApplicationEventCustomerAnnotation {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationEventCustomerAnnotation.class);
        context.getBean(MyService.class).doBusiness();
        context.close();
    }

    @Component
    static class MyService {
        @Autowired
        private ApplicationEventPublisher publisher;
        Logger logger = LoggerFactory.getLogger(EmailService.class);
        public void doBusiness() {
            logger.info("主线业务");
            publisher.publishEvent(new MyEvent("主线业务"));
        }
    }

    public static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }

    @Bean
    public SmartInitializingSingleton smartInitializingSingleton(AnnotationConfigApplicationContext context) {
        return new SmartInitializingSingleton() {
            @Override
            public void afterSingletonsInstantiated() {

                Iterator<String> beanNamesIterator = context.getDefaultListableBeanFactory().getBeanNamesIterator();

                while (beanNamesIterator.hasNext()) {
                    Object bean = context.getBean(beanNamesIterator.next());
                    Method[] methods = bean.getClass().getMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(MyListener.class)) {
                            ApplicationListener listener = new ApplicationListener() {
                                @Override
                                public void onApplicationEvent(ApplicationEvent event) {
                                    System.out.println(event.getClass());
                                    Class paramType = method.getParameterTypes()[0];
                                    if (paramType.isAssignableFrom(event.getClass())) {
                                        try {
                                            method.invoke(bean, event);
                                        }  catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            context.addApplicationListener(listener);
                        }
                    }
                }
            }
        };
    }

    @Component
    static class SmsService {
        Logger logger = LoggerFactory.getLogger(SmsService.class);

        @MyListener
        public void listener(MyEvent myEvent) {
            logger.info("接收事件 ------------------> 发短信");
        }
    }

    @Component
    static class EmailService {
        Logger logger = LoggerFactory.getLogger(EmailService.class);

        @MyListener
        public void listener(MyEvent myEvent) {
            logger.info("接收事件 ------------------> 发邮件");
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MyListener {}
}
