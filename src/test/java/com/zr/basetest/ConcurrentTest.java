package com.zr.basetest;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SpringBootTest
public class ConcurrentTest {

    @Test
    public void aqsTest() throws InterruptedException {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
        readLock.lock();
        writeLock.lock();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        new Thread(()->{
            reentrantLock.lock();
            countDownLatch.countDown();
        }).start();

        countDownLatch.await();
        System.out.println("main thread not stop !");
    }

    @Test
    public void concurrentHashMapTest(){
        Thread thread = new Thread(()->{
            System.out.println("thread 1");
        });

        thread.interrupt();
        thread.isInterrupted();
        Thread.currentThread().interrupted();


    }


    @Test
    public void threadLocalTest(){
        ThreadLocal<SimpleDateFormat> df = ThreadLocal.withInitial( () -> new SimpleDateFormat(""));
        df.remove();

        ThreadLocal threadLocal = new ThreadLocal();
        threadLocal.set(123);
        threadLocal.set(456);
        System.out.println(threadLocal.get());

        InheritableThreadLocal inheritableThreadLocal = new InheritableThreadLocal();
        inheritableThreadLocal.set(123);
        inheritableThreadLocal.get();


        TransmittableThreadLocal local = new TransmittableThreadLocal();

    }


    @Test
    public void automicTest() {
        AtomicStampedReference atomicStampedReference = new AtomicStampedReference("init", 1);
        boolean b = atomicStampedReference.compareAndSet("init", "init-val", 1, 2);
        System.out.println(b);
    }

    @Test
    public void blockQueue() throws InterruptedException {
        BlockingQueue blockingQueue = new ArrayBlockingQueue(10);
        blockingQueue.take();
        blockingQueue.poll();
    }

    @Test
    public void threadPool() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(()->{
            System.out.println("123");
        });

        FutureTask<String> futureTask = new FutureTask<>(() -> "123");
        futureTask.cancel(true);

        Future future = executor.submit(futureTask);
        future.cancel(true);
        future.isDone();

    }

    @Test
    public void completableFutureTest() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        CompletableFuture<Void> cf0 = CompletableFuture.runAsync(() -> {
            System.out.println("执行step 0，没有结果");
        }, executor);

        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("执行step 1");
            return "step1 result";
        }, executor);

        //whenComplete 不存在返回值, whenComplete方法返回的CompletableFuture的result是上个任务的结果
        //handle 存在返回值, handle方法返回的CompletableFuture的result是回调方法执行的结果
        CompletableFuture<String> step1Result = cf1.whenComplete((cf1Res, throwable) -> {
            System.out.println("step1 res is：" + cf1Res + "，without result!");
        });

        System.out.println("step1 res is ----> " + cf1.get());

        CompletableFuture<String> handle = cf1.handle((cf1Res, throwable) -> {
            System.out.println("step1 res is：" + cf1Res);
            return "after handle step1 result!";
        });

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("执行step 2");
            return "step2 result";
        });

        cf1.thenCombine(cf2, (result1, result2) -> {
            System.out.println(result1 + " , " + result2);
            System.out.println("执行step 3");
            return "step3 result";
        }).thenAccept(result3 -> System.out.println(result3));
    }

    @Test
    public void aopTest() {
        ProxyFactory proxyFactory = new ProxyFactory();
        Object proxy = proxyFactory.getProxy();

        Enhancer enhancer = new Enhancer();


    }
}
