package com.easyink.common.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


/**
 * redis分布式锁配置
 *
 * @author : wangzimo
 * @date : 2021-3-17 20:05:34
 */
@Configuration
public class RedisLockConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private String timeout;
    @Value("${spring.redis.database}")
    private int database;

    /**
     * RedissonClient,单机模式
     *
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + host + ":" + port);
//        String timeOutStr = timeout.replaceAll("s", "")
//                .replaceAll("ms", "");
//        singleServerConfig.setTimeout(Integer.parseInt(timeOutStr));
        singleServerConfig.setDatabase(database);
        //有密码
        if (password != null && !"".equals(password)) {
            singleServerConfig.setPassword(password);
        }
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonLocker(RedissonClient redissonClient) {
        //设置LockUtil的锁处理对象
        LockUtil.setLocker(redissonClient);
        return redissonClient;
    }
}
