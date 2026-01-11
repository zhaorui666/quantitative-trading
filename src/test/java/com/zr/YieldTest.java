package com.zr;

public class YieldTest extends Thread {

    public YieldTest(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 1; i <= 200; i++) {
            System.out.println("" + this.getName() + "-----" + i);
            // 当i为30时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
            if (i == 30) {
                System.out.println("" + this.getName() + "-----讓步");
                this.yield();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        YieldTest yt1 = new YieldTest("张三");
        YieldTest yt2 = new YieldTest("李四");

        yt1.start();
        yt2.start();

        Thread.sleep(50);
        System.out.println("主线程========= 张三 interrupt()");
        yt1.interrupt();
//        Thread.interrupted();
    }

}
