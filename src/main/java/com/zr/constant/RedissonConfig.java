package com.zr.constant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

//    //@Bean
//    public RedissonClient redissonClient() {
//        //单点
//        Config config = new Config();
//        //地址及密码
//        config.useSingleServer()
//                .setAddress("redis://127.0.0.1:6379").setPassword("123456");
//        return Redisson.create(config);
//
//        //主从
//        Config config = new Config();
//        config.useMasterSlaveServers()
//            .setMasterAddress("redis://127.0.0.1:6379").setPassword("123456")
//            .addSlaveAddress("redis://127.0.0.1:6389")
//            .addSlaveAddress("redis://127.0.0.1:6399");
//        return Redisson.create(config);

        //哨兵
//        Config config = new Config();
//        config.useSentinelServers()
//            .setMasterName("myMaster")
//            .addSentinelAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6389")
//            .addSentinelAddress("redis://127.0.0.1:6399");
//        return Redisson.create(config);

        //集群
//        Config config = new Config();
//        config.useClusterServers()
//                //cluster state scan interval in milliseconds
//            .setScanInterval(2000)
//            .addNodeAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6389")
//            .addNodeAddress("redis://127.0.0.1:6399");
//        return Redisson.create(config);
//    }

    @Bean
    public String secondBean() {
        return "from second round";
    }
}
