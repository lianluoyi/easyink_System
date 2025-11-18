package com.easyink.framework.config;

import com.easyink.framework.config.properties.RedisProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmlly.common.utils.overide.MyStringRedisSerializer;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.NettyCustomizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Redis配置类
 * 支持单机、集群、哨兵模式
 * Author: lixingjia
 * Date: 2019-01-22
 * Time: 16:54
 */
@Slf4j
@Component
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * Redis连接工厂配置
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
        System.out.println("=== Redis连接配置开始 ===");
        System.out.println("Redis模式: " + properties.getMode());
        
        RedisConnectionFactory factory;
        switch (properties.getMode().toLowerCase()) {
            case "cluster":
                factory = createClusterConnectionFactory(properties);
                break;
            case "sentinel":
                factory = createSentinelConnectionFactory(properties);
                break;
            case "single":
            default:
                factory = createStandaloneConnectionFactory(properties);
                break;
        }
        
        // 测试连接并打印连接信息
        testAndLogConnection(factory, properties);
        
        System.out.println("✅ Redis前缀配置: " + properties.getPrefix());
        System.out.println("=== Redis连接配置完成 ===");
        return factory;
    }

    /**
     * 创建单机模式连接工厂
     */
    private RedisConnectionFactory createStandaloneConnectionFactory(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setDatabase(properties.getDatabase());
        
        if (properties.getPassword() != null) {
            config.setPassword(properties.getPassword());
        }
        
        LettucePoolingClientConfiguration clientConfig = createLettuceClientConfiguration(properties);
        return new LettuceConnectionFactory(config, clientConfig);
    }

    /**
     * 创建集群模式连接工厂
     */
    private RedisConnectionFactory createClusterConnectionFactory(RedisProperties properties) {
        List<String> nodes = properties.getCluster().getNodeList();
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Redis cluster nodes cannot be empty");
        }
        
        RedisClusterConfiguration config = new RedisClusterConfiguration(nodes);
        
        if (properties.getCluster().getPassword() != null) {
            config.setPassword(properties.getCluster().getPassword());
        }
        
        config.setMaxRedirects(properties.getCluster().getMaxRedirects());
        
        LettucePoolingClientConfiguration clientConfig = createLettuceClientConfiguration(properties);
        return new LettuceConnectionFactory(config, clientConfig);
    }

    /**
     * 创建哨兵模式连接工厂
     */
    private RedisConnectionFactory createSentinelConnectionFactory(RedisProperties properties) {
        List<String> nodes = properties.getSentinel().getNodeList();
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Redis sentinel nodes cannot be empty");
        }

        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.master(properties.getSentinel().getMaster());
        
        for (String node : nodes) {
            String[] parts = node.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid sentinel node format: " + node + ", expected format: host:port");
            }
            config.sentinel(parts[0], Integer.parseInt(parts[1]));
        }
        
        if (properties.getSentinel().getPassword() != null) {
            config.setPassword(properties.getSentinel().getPassword());
        }
        
        config.setDatabase(properties.getSentinel().getDatabase());
        
        LettucePoolingClientConfiguration clientConfig = createLettuceClientConfiguration(properties);
        return new LettuceConnectionFactory(config, clientConfig);
    }

    /**
     * 创建Lettuce客户端配置
     */
    /**
     * 创建Lettuce客户端配置
     */
    private LettucePoolingClientConfiguration createLettuceClientConfiguration(RedisProperties properties) {
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
                LettucePoolingClientConfiguration.builder()
                        .clientResources(clientResources())
                        .commandTimeout(properties.getTimeout());

        // 设置客户端名称用于软隔离
//        if (properties.getPrefix() != null && !properties.getPrefix().trim().isEmpty()) {
//            String clientName = normalizeClientName(properties.getPrefix());
//            builder.clientName(clientName);
//            System.out.println("✅ Redis软隔离已启用，客户端名称: " + clientName);
//        } else {
//            System.out.println("ℹ️ Redis软隔离未启用，prefix为空");
//        }

        return builder.build();
    }

    /**
     * 标准化客户端名称
     * 将特殊字符替换为下划线，确保客户端名称符合Redis规范
     */
    private String normalizeClientName(String prefix) {
        if (prefix == null) {
            return "default_client";
        }

        // 移除前缀中的特殊字符，只保留字母、数字和下划线
        String normalized = prefix.replaceAll("[^a-zA-Z0-9_]", "_");

        // 确保不以数字开头
        if (normalized.matches("^\\d.*")) {
            normalized = "client_" + normalized;
        }

        // 限制长度，避免过长
        if (normalized.length() > 50) {
            normalized = normalized.substring(0, 50);
        }

        return normalized;
    }

    public MyStringRedisSerializer myRedisStringKeySerializer (RedisProperties properties)
    {
        return new MyStringRedisSerializer(properties.getPrefix());
    }



    @Bean
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }

    @Bean("empleRedisTemplate")
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> empleRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
    /**
     * lettuce 心跳检测
     * @return
     */
    @Bean
    public ClientResources clientResources(){
        NettyCustomizer nettyCustomizer = new NettyCustomizer() {
            @Override
            public void afterChannelInitialized(Channel channel) {
                //第一个参数readerIdleTimeSeconds设置为小于超时时间timeout，单位为秒，
                //每隔readerIdleTimeSeconds会进行重连,在超时之前重连就能避免命令超时报错。
                channel.pipeline().addLast(
                        new IdleStateHandler(100, 0, 0));
                channel.pipeline().addLast(new ChannelDuplexHandler() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        if (evt instanceof IdleStateEvent) {
                            ctx.disconnect();
                        }
                    }
                });
            }
            @Override
            public void afterBootstrapInitialized(Bootstrap bootstrap) {
            }
        };
        return ClientResources.builder().nettyCustomizer(nettyCustomizer ).build();
    }

    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()));
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()));

        return redisCacheConfiguration;
    }


    /**
     * 限流脚本
     */
    private String limitScriptText() {
        return "local key = KEYS[1]\n" +
                "local count = tonumber(ARGV[1])\n" +
                "local time = tonumber(ARGV[2])\n" +
                "local current = redis.call('get', key);\n" +
                "if current and tonumber(current) > count then\n" +
                "    return tonumber(current);\n" +
                "end\n" +
                "current = redis.call('incr', key)\n" +
                "if tonumber(current) == 1 then\n" +
                "    redis.call('expire', key, time)\n" +
                "end\n" +
                "return tonumber(current);";
    }

    /**
     * 测试Redis连接并打印连接信息
     */
    private void testAndLogConnection(RedisConnectionFactory factory, RedisProperties properties) {
        try {
            // 根据模式打印不同的连接信息
            switch (properties.getMode().toLowerCase()) {
                case "single":
                    System.out.println("✅ Redis单机模式连接成功!");
                    System.out.println("   主机: " + properties.getHost() + ":" + properties.getPort());
                    System.out.println("   数据库: " + properties.getDatabase());
                    break;
                    
                case "cluster":
                    System.out.println("✅ Redis集群模式连接成功!");
                    List<String> clusterNodes = properties.getCluster().getNodeList();
                    System.out.println("   集群节点数量: " + (clusterNodes != null ? clusterNodes.size() : 0));
                    if (clusterNodes != null) {
                        for (int i = 0; i < clusterNodes.size(); i++) {
                            System.out.println("   节点" + (i + 1) + ": " + clusterNodes.get(i));
                        }
                    }
                    System.out.println("   最大重定向次数: " + properties.getCluster().getMaxRedirects());
                    System.out.println("   刷新周期: " + properties.getCluster().getRefreshPeriod());
                    break;
                    
                case "sentinel":
                    System.out.println("✅ Redis哨兵模式连接成功!");
                    List<String> sentinelNodes = properties.getSentinel().getNodeList();
                    System.out.println("   哨兵节点数量: " + (sentinelNodes != null ? sentinelNodes.size() : 0));
                    if (sentinelNodes != null) {
                        for (int i = 0; i < sentinelNodes.size(); i++) {
                            System.out.println("   哨兵" + (i + 1) + ": " + sentinelNodes.get(i));
                        }
                    }
                    System.out.println("   主节点名称: " + properties.getSentinel().getMaster());
                    break;
            }
            
            // 打印连接池信息
            System.out.println("   连接池配置:");
            System.out.println("     最小空闲连接: " + properties.getLettuce().getPool().getMinIdle());
            System.out.println("     最大空闲连接: " + properties.getLettuce().getPool().getMaxIdle());
            System.out.println("     最大活跃连接: " + properties.getLettuce().getPool().getMaxActive());
            System.out.println("     最大等待时间: " + properties.getLettuce().getPool().getMaxWait().toMillis() + "ms");
            
        } catch (Exception e) {
            System.out.println("❌ Redis连接失败: " + ExceptionUtils.getStackTrace(e));
        }
    }

}
