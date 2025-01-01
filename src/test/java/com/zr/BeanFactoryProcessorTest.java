package com.zr;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import javax.sql.DataSource;

public class BeanFactoryProcessorTest {

    public static void main(String[] args) {

        GenericApplicationContext context = new GenericApplicationContext();

        context.registerBean("config", Config.class);

        //设置ConfigurationClassPostProcessor后处理器
//        context.registerBean(ConfigurationClassPostProcessor.class);

        //设置MapperScanner后处理器
        context.registerBean(MapperScannerConfigurer.class, bd -> {bd.getPropertyValues().add("basePackage", "com.zr.mapper");});

        context.refresh();

        //org.springframework.context.annotation.ConfigurationClassPostProcessor
        //org.mybatis.spring.mapper.MapperScannerConfigurer
        //config
        //dataSource
        //sqlSessionFactory
        //plateInfoMapper
        //plateMarketMapper
        //stcokMarketMapper
        //stockBaseInfoMapper
        //stockFinaStaMapper
        for (String name: context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }
}

@Configuration
@ComponentScan("com.zr.component")
class Config {

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
