package com.zr;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class InjectTest implements ApplicationContextAware {

    @Test
    public void valueTest() throws NoSuchFieldException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(InjectTest.class);

        ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
        resolver.setBeanFactory(context.getBeanFactory());

        //获取被注入对象字段上的元信息
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(Bean1.class.getDeclaredField("userTimezone"), true);
        String suggestedValue = (String) resolver.getSuggestedValue(dependencyDescriptor);

        String resolvedValue = context.getEnvironment().resolvePlaceholders(suggestedValue);
        System.out.println(resolvedValue);//Asia/Shanghai

        dependencyDescriptor.getResolvableType().getGeneric();
    }

    @Test
    public void autowiredTest() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(InjectTest.class);
        DefaultListableBeanFactory defaultListableBeanFactory = context.getDefaultListableBeanFactory();

        //字段注入
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(Bean1.class.getDeclaredField("bean2"), true);
        Object resolveDependency = defaultListableBeanFactory.doResolveDependency(dependencyDescriptor, "bean1", null, null);
        System.out.println(resolveDependency.getClass());//class com.zr.InjectTest$Bean2

        //方法注入
        DependencyDescriptor dependencyDescriptor2 = new DependencyDescriptor(new MethodParameter(Bean1.class.getDeclaredMethod("setBean2", Bean2.class), 0), true);
        Object resolveDependency2 = defaultListableBeanFactory.doResolveDependency(dependencyDescriptor2, "bean1", null, null);
        System.out.println(resolveDependency2.getClass());//class com.zr.InjectTest$Bean2
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }


    public class Bean1 {

        @Value("${user.timezone}")
        private String userTimezone;

        @Autowired
        private Bean2 bean2;

        @Autowired
        private void setBean2 (Bean2 bean2) {
            this.bean2 = bean2;
        }
    }

    @Component
    static class Bean2 {}
}
