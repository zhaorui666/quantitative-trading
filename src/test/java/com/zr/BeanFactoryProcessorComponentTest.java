package com.zr;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.SneakyThrows;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Set;

public class BeanFactoryProcessorComponentTest {

    public static void main(String[] args) {

        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", ConfigTest.class);
        context.registerBean("componentScanPostProcessor", CustomerComponentScanPostProcessor.class);
        context.registerBean("customerBeanPostProcessor", CustomerBeanPostProcessor.class);
        context.refresh();

        //config
        //componentScanPostProcessor
        //customerBeanPostProcessor
        //componentTest
        //dataSource
        //sqlSessionFactory
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }
}

/**
 * 自定义componentScanner beanFactoryProcessor
 */
class CustomerComponentScanPostProcessor implements BeanFactoryPostProcessor {

    @SneakyThrows
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ComponentScan componentScan = AnnotationUtils.findAnnotation(ConfigTest.class, ComponentScan.class);
        if (componentScan != null) {
            //元数据读取器的缓存实现：CachingMetadataReaderFactory 是 MetadataReaderFactory 接口的一个缓存实现。它为每个 Spring 资源句柄（即每个 “.class” 文件）缓存 MetadataReader 实例。这意味着当多次请求同一个类的元数据时，可以重用已经创建的 MetadataReader 实例，从而提高性能。
            //提高效率：通过缓存机制，CachingMetadataReaderFactory 可以避免重复创建 MetadataReader 实例，这在处理大量类文件时特别有用，可以显著提高应用程序的效率。
            //自定义和使用：可以创建 CachingMetadataReaderFactory 的实例，并通过它获取特定类的元数据。例如，在 Spring 应用程序中，可以使用它来获取指定包下的所有类的元数据，进而进行自定义处理，如获取类上的注解等
            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();

            //Component注解 beanName生成器
            BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

            for (String basePackage : componentScan.basePackages()) {
                System.out.println(basePackage);
                String path = "classpath*:" + basePackage.replace(".", "/") + "**/*.class";
                System.out.println(path);
                Resource[] resources = new PathMatchingResourcePatternResolver().getResources(path);
                for (Resource r : resources) {
                    MetadataReader reader = factory.getMetadataReader(r);
                    boolean hasAnnotation = reader.getAnnotationMetadata().hasAnnotation(Component.class.getName());
                    boolean hasMetaAnnotation = reader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName());

                    if (hasAnnotation || hasMetaAnnotation) {
                        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(reader.getClassMetadata().getClassName()).getBeanDefinition();
                        //生成beanName
                        String generateBeanName = beanNameGenerator.generateBeanName(beanDefinition, (BeanDefinitionRegistry) beanFactory);
                        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(generateBeanName, beanDefinition);
                    }
                }
            }
        }
    }
}

/**
 * 自定义@Bean beanFactoryProcessor
 */
class CustomerBeanPostProcessor implements BeanFactoryPostProcessor {

    @SneakyThrows
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();

        MetadataReader reader = factory.getMetadataReader("com.zr.Config");
        Set<MethodMetadata> annotatedMethods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());

        if (CollectionUtil.isNotEmpty(annotatedMethods)) {
            annotatedMethods.stream().forEach(method -> {
                System.out.println("------------------------>" + method.getMethodName());
                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition()
                        .setFactoryMethodOnBean(method.getMethodName(), "config")
                        //如果存在Bean引用关系, 则需要设置装配模式(如sqlSessionFactory)
                        .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR)
                        .getBeanDefinition();
                ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(method.getMethodName(), beanDefinition);
            });
        }
    }
}

@Configuration
@ComponentScan("com.zr.component")
class ConfigTest {

    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }
}