package com.zr;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

public class SpringTest {

    public static void main(String[] args) {

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(BeanDefinitionTest.class).setScope(ConfigurableBeanFactory.SCOPE_SINGLETON).getBeanDefinition();
        //Generic bean: class [com.zr.SpringTest$BeanDefinitionTest]; scope=singleton; abstract=false; lazyInit=null; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null
        System.out.println(beanDefinition);
        beanFactory.registerBeanDefinition("beanDefinitionTest", beanDefinition);

        //注册相关beanFactoryPostProcessor、beanPostProcess
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        //org.springframework.context.annotation.internalConfigurationAnnotationProcessor
        //org.springframework.context.annotation.internalAutowiredAnnotationProcessor
        //org.springframework.context.annotation.internalCommonAnnotationProcessor
        //org.springframework.context.event.internalEventListenerProcessor
        //org.springframework.context.event.internalEventListenerFactory
        Arrays.asList(beanFactory.getBeanDefinitionNames()).forEach(f -> System.out.println(f));

        //执行BeanFactoryPostProcessor
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(f -> f.postProcessBeanFactory(beanFactory));

        //添加beanPostProcessor(执行节点为getBean的生命周期)
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().forEach(f -> beanFactory.addBeanPostProcessor(f));

        //org.springframework.context.annotation.internalConfigurationAnnotationProcessor
        //org.springframework.context.annotation.internalAutowiredAnnotationProcessor
        //org.springframework.context.annotation.internalCommonAnnotationProcessor
        //org.springframework.context.event.internalEventListenerProcessor
        //org.springframework.context.event.internalEventListenerFactory
        //beanDefinitionTest
        //bean1
        Arrays.asList(beanFactory.getBeanDefinitionNames()).forEach(f -> System.out.println(f)); //将自定义的bean也注入到了容器中
    }

    @Configuration
    class BeanDefinitionTest {

        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

    }

    class Bean1 {}
}
