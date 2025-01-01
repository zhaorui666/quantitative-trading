package com.zr.aoptest;

import com.zr.Foo;
import com.zr.Target;
import org.aopalliance.intercept.MethodInvocation;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 *  JDK代理 代理类和目标类必须实现同一个接口
 *  Cglib代理 目标类必须可派生子类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AopTest {

    @Test
    public void jdkProxyTest() {

        Target target = new Target();
        ClassLoader loader = this.getClass().getClassLoader();

        Foo proxy = (Foo)Proxy.newProxyInstance(loader, new Class[]{Foo.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(proxy.getClass()); //class com.zr.$Proxy29
                System.out.println("before ...");
                method.invoke(target, args);
                return null;
            }
        });

        //before ...
        //target foo....
        proxy.foo();
    }

    @Test
    public void simulateJdkProxy () {

        Target target = new Target();

        Foo proxy = new $Proxy0(new InvocationHandler() {
            //实际上invoke方法参数均由代理类进行指定
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("simulate before ...");
                //使用反射调用,但jdk也存在优化处理。即调用达到一定次数时转换为静态方法调用
                Object res = method.invoke(target);
                return res;
            }
        });

        //simulate before ...
        //target foo....
        proxy.foo();
    }

    @Test
    public void cglibProxyTest() {
        Target target = new Target();

        Target proxy = (Target)Enhancer.create(Target.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object enhancer, Method  method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("before enhancer ....");
//                method.invoke(target, args);//反射
                Object result = methodProxy.invoke(target, null);//未使用反射，结合目标类使用
//                Object result = methodProxy.invokeSuper(enhancer, args);//未使用反射，结合代理对象使用
                System.out.println("after enhancer ....");
                System.out.println("enhancer.getClass() is ----------------->" + enhancer.getClass()); //class com.zr.Target$$EnhancerByCGLIB$$8daf5ad9
                return result;
            }
        });

        System.out.println("proxy.getClass() is ----------------->" + proxy.getClass()); //class com.zr.Target$$EnhancerByCGLIB$$8daf5ad9
        //before enhancer ....
        //target foo....
        //after enhancer ....
        proxy.foo();
    }

    @Test
    public void aopOriginCode() throws NoSuchMethodException {
        //定义切点
        AspectJExpressionPointcut pointcut1 = new AspectJExpressionPointcut();
        pointcut1.setExpression("execution(* com.zr.Target.foo())");
        System.out.println(pointcut1.matches(Target.class.getMethod("foo"), Target.class));

        AspectJExpressionPointcut pointcut2 = new AspectJExpressionPointcut();
        pointcut2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        System.out.println(pointcut2.matches(Target.class.getMethod("foo"), Target.class));

        StaticMethodMatcherPointcut pointcut3 = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                MergedAnnotations mergedAnnotations = MergedAnnotations.from(method);
                if (mergedAnnotations.isPresent(Transactional.class)) {
                    return true;
                }

                mergedAnnotations = MergedAnnotations.from(Target.class, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                if (mergedAnnotations.isPresent(Transactional.class)) {
                    return true;
                }

                return false;
            }
        };
        System.out.println(pointcut3.matches(Target.class.getMethod("foo"), Target.class));


        //定义通知 ---> MethodInterceptor extend Advice 继承通知接口
        org.aopalliance.intercept.MethodInterceptor methodInterceptor = invocation -> {
            System.out.println("before....");
            Object result = invocation.proceed();
            System.out.println("after...");
            return result;
        };

        //定义切面 = 切点 + 通知.    advisor通知者，通常持有一个advice
        DefaultPointcutAdvisor pointcutAdvisor = new DefaultPointcutAdvisor(pointcut1, methodInterceptor);

        Target target = new Target();
        //创建代理类 设置代理目标、添加通知者
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        //Set whether to proxy the target class directly, instead of just proxying specific interfaces. Default is "false".
        //设置是否直接代理目标类，而不是仅代理目标接口。默认为false。如果直接代理目标类，则固定使用cglib代理
        //如果为false，则目标实现接口则尝试使用jdk代理(注意：仅为尝试，具体是否可以使用jdk代理取决于jdk代理硬性条件)，否则使用cglib代理
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setInterfaces(target.getClass().getInterfaces());
        proxyFactory.addAdvisor(pointcutAdvisor);
        Target proxy = (Target) proxyFactory.getProxy();
        proxy.foo();
        System.out.println(proxy.getClass()); //class com.zr.aoptest.AopTest$Target$$EnhancerBySpringCGLIB$$d2589267
    }

    @Test
    public void contextTest() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("advisorConfig", AdvisorConfig.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();

        //advisorConfig
        //org.springframework.context.annotation.ConfigurationClassPostProcessor
        //advisor
        //methodInterceptor
        //target
        //annotationAwareAspectJAutoProxyCreator
        Arrays.asList(context.getBeanDefinitionNames()).forEach(f -> System.out.println(f));

        Target target = context.getBean(Target.class);
        System.out.println(target.getClass());//class com.zr.Target$$EnhancerBySpringCGLIB$$6ae6f727
        //methodInterceptor before
        //target foo....
        //methodInterceptor after
        target.foo();
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

        //自动创建代理类
        @Bean
        AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }
    }
}
