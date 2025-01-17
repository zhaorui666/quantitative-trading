package com.zr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Configuration
public class ApplicationEventTest {

    static Logger logger = LoggerFactory.getLogger(ApplicationEventTest.class);

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationEventTest.class);
        MyService myService = context.getBean(MyService.class);
        myService.doBusiness();
    }

    @Component
    static class MyService {

        @Autowired
        private ApplicationEventPublisher publisher;

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

    @Component
    static class SmsApplicationListener implements ApplicationListener<MyEvent> {
        @Override
        public void onApplicationEvent(MyEvent myEvent) {
            logger.info("接收事件...........发短信");
        }
    }

    @Component
    static class EmailService {
        @EventListener
        public void listener(MyEvent myEvent) {
            logger.info("接收事件...........发邮件");
        }
    }

    @Bean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        return executor;
    }

//    @Bean
//    public SimpleApplicationEventMulticaster applicationEventMulticaster(ThreadPoolTaskExecutor executor) {
//        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
//        multicaster.setTaskExecutor(executor);
//        return multicaster;
//    }

    /**
     * 覆盖容器中 ApplicationEventMulticaster的默认实现类 广播器, 使用自定义逻辑进行发布
     * 可以是作为事件监听器、发布器的总管中介。用以协调发布--->监听
     */
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(ConfigurableApplicationContext context) {
        List<ApplicationListener> listeners = new ArrayList<>();
        CustomerApplicationEventMulticaster multicaster = new CustomerApplicationEventMulticaster() {

            //收集容器中的监听器
            @Override
            public void addApplicationListenerBean(String listenerBeanName) {
                System.out.println(listenerBeanName);
                ApplicationListener listenBean = context.getBean(listenerBeanName, ApplicationListener.class);
                listeners.add(listenBean);
            }

            //调用监听器进行广播
            @Override
            public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
                for(ApplicationListener listener: listeners) {
                    ResolvableType listenParamResolvableType = ResolvableType.forClass(listener.getClass()).getInterfaces()[0].getGeneric();

                    //使用SimpleApplicationEventMulticaster提供的方法
                    //由于evnetType可为空, 则需通过事件进行解析出eventType
                    eventType = eventType == null ? ResolvableType.forInstance(event) : eventType;

                    if (eventType != null && listenParamResolvableType.isAssignableFrom(eventType)) {
                        logger.info("------------------------> 开始调用监听器");
                        listener.onApplicationEvent(event);
                    }
                }
            }
        };

        return multicaster;
    }

    static abstract class CustomerApplicationEventMulticaster implements ApplicationEventMulticaster {

        @Override
        public void addApplicationListener(ApplicationListener<?> listener) {

        }

        @Override
        public void addApplicationListenerBean(String listenerBeanName) {

        }

        @Override
        public void removeApplicationListener(ApplicationListener<?> listener) {

        }

        @Override
        public void removeApplicationListenerBean(String listenerBeanName) {

        }

        @Override
        public void removeApplicationListeners(Predicate<ApplicationListener<?>> predicate) {

        }

        @Override
        public void removeApplicationListenerBeans(Predicate<String> predicate) {

        }

        @Override
        public void removeAllListeners() {

        }

        @Override
        public void multicastEvent(ApplicationEvent event) {

        }

        @Override
        public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {

        }
    }
}
