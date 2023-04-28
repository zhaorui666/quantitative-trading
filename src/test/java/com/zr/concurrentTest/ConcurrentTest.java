package com.zr.concurrentTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrentTest {

    @Test
    public void readWriteLock(){
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
        readLock.lock();
        writeLock.lock();
    }

    @Test
    public void concurrentHashMapTest(){


    }


    @Test
    public void threadLocalTest(){
        ThreadLocal<SimpleDateFormat> df = ThreadLocal.withInitial( () -> new SimpleDateFormat(""));
        df.remove();

        ThreadLocal threadLocal = new ThreadLocal();
    }

}
