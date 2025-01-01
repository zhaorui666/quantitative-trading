package com.zr.aoptest;

import com.zr.Foo;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class $Proxy0 extends Proxy implements Foo {

    static Method foo;

    static {
        try {
            foo = Foo.class.getMethod("foo");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    $Proxy0(InvocationHandler h) {
        super(h);
    }

    @SneakyThrows
    @Override
    public void foo() {
        h.invoke(this, foo, null);
    }
}