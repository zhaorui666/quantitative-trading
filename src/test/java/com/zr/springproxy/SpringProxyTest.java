package com.zr.springproxy;
import org.springframework.aop.framework.Advised;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringProxyTest {
    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext context = SpringApplication.run(SpringProxyTest.class, args);
        //2024-12-31 23:06:48.156  INFO 43696 --- [           main] com.zr.springproxy.ProxyTarget           : setField
        //2024-12-31 23:06:48.156  INFO 43696 --- [           main] com.zr.springproxy.ProxyTarget           : init
        ProxyTarget proxy = context.getBean(ProxyTarget.class);


        proxy.setField(new ProxyTargetField()); //proxyTarget before...
        proxy.setInitialized(true); //proxyTarget before...

        System.out.println(proxy.field);//null
        System.out.println(proxy.initialized);//null

        System.out.println(proxy.getField()); //proxyTarget before...  com.zr.springproxy.ProxyTargetField@28f878a0
        System.out.println(proxy.getInitialized());//proxyTarget before...  true

        if (proxy instanceof Advised) {
            Advised advised = (Advised)proxy;
            ProxyTarget target = (ProxyTarget) advised.getTargetSource().getTarget();
            System.out.println(target.field); //com.zr.springproxy.ProxyTargetField@43f0c2d1
            System.out.println(target.initialized); //true
        }
    }
}