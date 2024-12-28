package com.zr;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AopTest {

    static class Target {
        public void foo() {
            System.out.println("target foo....");
        }

        Target() {
            System.out.println("Target()");
        }

        @PostConstruct
        public void init () {
            System.out.println("Target init");
        }
    }


    @Test
    public void cglibProxyTest() {
        Target target = new Target();

        Target proxy = (Target)Enhancer.create(Target.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object enhancer, Method  method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("before enhancer ....");
//                method.invoke(target, args);//反射
                Object result = methodProxy.invoke(target, null);//未使用反射
//                Object result = methodProxy.invokeSuper(enhancer, args);
                System.out.println("after enhancer ....");
                System.out.println(enhancer.getClass());
                return result;
            }
        });

        System.out.println(proxy.getClass());
        proxy.foo();
    }


    @Test
    public void aopOriginCode() throws NoSuchMethodException {
        //切点
        AspectJExpressionPointcut pointcut1 = new AspectJExpressionPointcut();
        pointcut1.setExpression("execution(* foo())");
        System.out.println(pointcut1.matches(Target.class.getMethod("foo"), Target.class));

        AspectJExpressionPointcut pointcut2 = new AspectJExpressionPointcut();
        pointcut2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        System.out.println(pointcut2.matches(Target.class.getMethod("foo"), Target.class));

        StaticMethodMatcherPointcut pointcut3 = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                MergedAnnotations mergedAnnotations = MergedAnnotations.from(method);
                if (mergedAnnotations.isPresent(Transactional.class)) {
//                    return true;
                }

                mergedAnnotations = MergedAnnotations.from(Target.class, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                if (mergedAnnotations.isPresent(Transactional.class)) {
                    return true;
                }

                return false;
            }
        };
        System.out.println(pointcut3.matches(Target.class.getMethod("foo"), Target.class));


        //通知 ---> MethodInterceptor extend Advice 继承通知接口
        org.aopalliance.intercept.MethodInterceptor methodInterceptor = invocation -> {
            System.out.println("before....");
            Object result = invocation.proceed();
            System.out.println("after...");
            return result;
        };

        //切面 = 切点 + 通知
        //advisor通知者，通常持有一个advice
        DefaultPointcutAdvisor pointcutAdvisor = new DefaultPointcutAdvisor(pointcut1, methodInterceptor);

        Target target = new Target();
        //创建代理类 设置代理目标、添加通知者
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        //Set whether to proxy the target class directly, instead of just proxying specific interfaces. Default is "false".
        //设置是否直接代理目标类，而不是仅代理目标接口。默认为false。如果直接代理目标类，则固定使用cglib代理
        //如果为false，则目标实现接口则使用jdk代理，否则使用cglib代理
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setInterfaces(target.getClass().getInterfaces());
        proxyFactory.addAdvisor(pointcutAdvisor);
        Target proxy = (Target) proxyFactory.getProxy();
        proxy.foo();
        System.out.println(proxy.getClass()); //class com.zr.AopTest$Target$$EnhancerBySpringCGLIB$$d2589267
    }

//    @Aspect
    static class AspectTest {
        @Before("execution(* foo())")
        public void before() {
            System.out.println("foo before...");
        }
    }

    @Configuration
    static class AdvisorConfig {

        @Bean
        public Advisor advisor(org.aopalliance.intercept.MethodInterceptor methodInterceptor) throws NoSuchMethodException {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            return new DefaultPointcutAdvisor(pointcut, methodInterceptor);
        }

        @Bean
        public org.aopalliance.intercept.MethodInterceptor methodInterceptor() {
            return new org.aopalliance.intercept.MethodInterceptor() {
                @Nullable
                @Override
                public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
                    System.out.println("methodInterceptor before");
                    Object result = invocation.proceed();
                    System.out.println("methodInterceptor after");
                    return result;
                }
            };
        }

        @Bean
        public Target target() {
            return new Target();
        }

        @Bean
        ConfigurationClassPostProcessor configurationClassPostProcessor() {
            return new ConfigurationClassPostProcessor();
        }

        @Bean
        CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor() {
            return new CommonAnnotationBeanPostProcessor();
        }

        @Bean
        AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }
    }

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("advisorConfig", AdvisorConfig.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();

        //aspectTest
        //advisorConfig
        //org.springframework.context.annotation.ConfigurationClassPostProcessor
        //org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
        //advisor
        //methodInterceptor
        Arrays.asList(context.getBeanDefinitionNames()).forEach(f -> System.out.println(f));

        Target target = context.getBean(Target.class);
        System.out.println(target.getClass());
        target.foo();
    }
}
