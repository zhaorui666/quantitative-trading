package com.zr.springproxy;

import com.zr.Target;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Aspect
@Component
class AspectTest {

    @Before("execution(* com.zr.springproxy.ProxyTarget.*(..))")
    public void before() {
        System.out.println("proxyTarget before...");
    }
}


@Component
class ProxyTarget {

    Logger log = LoggerFactory.getLogger(ProxyTarget.class);

    ProxyTargetField field;

    Boolean initialized;

    public Boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }

    public ProxyTargetField getField() {
        log.info("getField");
        return field;
    }

    @Autowired
    public void setField(ProxyTargetField field) {
        log.info("setField");
        this.field = field;
    }

    @PostConstruct
    public void init() {
        log.info("init");
        initialized = true;
    }

}

@Component
class ProxyTargetField {

}
