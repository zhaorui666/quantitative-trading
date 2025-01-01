package com.zr;

import lombok.Data;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

public class SpringStartProcessTest {

    public static void main(String[] args) throws Exception{

        SpringApplication app = new SpringApplication();

        //共计7类事件
        //class org.springframework.boot.context.event.ApplicationStartingEvent
        //class org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
        //class org.springframework.boot.context.event.ApplicationContextInitializedEvent
        //class org.springframework.boot.context.event.ApplicationPreparedEvent
        //class org.springframework.boot.context.event.ApplicationStartedEvent
        //class org.springframework.boot.context.event.ApplicationReadyEvent
        //class org.springframework.boot.context.event.ApplicationFailedEvent
        app.addListeners(e -> System.out.println(e.getClass()));

        //获取对应的实现接口
        List<String> names = SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class, SpringTest.class.getClassLoader());
        for (String name : names) {
            System.out.println(name);
            Class clazz = Class.forName(name);
            Constructor constructor = clazz.getConstructor(app.getClass(), args.getClass());
            SpringApplicationRunListener publisher = (SpringApplicationRunListener)constructor.newInstance(app, args);

            DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
            publisher.starting(bootstrapContext);
            publisher.environmentPrepared(bootstrapContext, new StandardEnvironment());

            GenericApplicationContext context = new GenericApplicationContext();
            publisher.contextPrepared(context);
            publisher.contextLoaded(context);
            context.refresh();
            publisher.started(context, Duration.of(1, ChronoUnit.NANOS));
            publisher.ready(context, Duration.of(1, ChronoUnit.NANOS));
            publisher.failed(context, new Exception());
        }

        StandardEnvironment env = new StandardEnvironment();

        MutablePropertySources propertySources = env.getPropertySources();
        //PropertiesPropertySource {name='systemProperties'} ----> 系统属性：VM参数
        //SystemEnvironmentPropertySource {name='systemEnvironment'}
        for (PropertySource ps: propertySources) {
            System.out.println(ps);
        }

        System.out.println("==================================================================");

        Properties properties = new Properties();
        properties.setProperty("key", "value");
        PropertiesPropertySource propertySource = new PropertiesPropertySource("properties", properties);
        propertySources.addFirst(propertySource);

        //value
        System.out.println(env.getProperty("key"));

        propertySources.addFirst(new ResourcePropertySource(new ClassPathResource("swsvs.ini")));

        //ResourcePropertySource {name='class path resource [swsvs.ini]'}
        //PropertiesPropertySource {name='systemProperties'}
        //SystemEnvironmentPropertySource {name='systemEnvironment'}
        for (PropertySource ps: propertySources) {
            System.out.println(ps);
        }

        //10.118.8.76
        System.out.println(env.getProperty("ip"));

        //1.环境对象与class对象进行属性绑定
        SwsvsInfo swsvsInfo = Binder.get(env).bind("", SwsvsInfo.class).get();
        //2.与已创建的对象进行属性绑定
        Binder.get(env).bind("", Bindable.ofInstance(swsvsInfo));

        //SwsvsInfo(ip=10.118.8.76, port=8008, appName=sm2001-sign)
        System.out.println(swsvsInfo);
    }
}


@Data
class SwsvsInfo {
    private String ip;
    private String port;
    private String appName;
}


