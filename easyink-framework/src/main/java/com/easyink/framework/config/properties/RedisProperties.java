package com.easyink.framework.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Redis配置属性类
 * 支持单机、集群、哨兵模式
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
    
    /**
     * Redis模式: single(单机), cluster(集群), sentinel(哨兵)
     */
    private String mode = "single";
    
    /**
     * 单机模式配置
     */
    private String host;
    private int port;
    private String password;
    private int database;
    private Duration timeout;
    private String prefix ;

    /**
     * 集群模式配置
     */
    private Cluster cluster = new Cluster();
    
    /**
     * 哨兵模式配置
     */
    private Sentinel sentinel = new Sentinel();
    
    /**
     * 连接池配置
     */
    private Lettuce lettuce = new Lettuce();
    
    @Data
    public static class Cluster {
        private String nodes; // 格式：host:port,host:port
        private String password;
        private Duration timeout = Duration.ofMillis(5000);
        private int maxRedirects = 3;
        private Duration refreshPeriod = Duration.ofSeconds(30);
        
        public List<String> getNodeList() {
            if (nodes == null || nodes.trim().isEmpty()) {
                return null;
            }
            return java.util.Arrays.asList(nodes.split(","));
        }
    }
    
    @Data
    public static class Sentinel {
        private String nodes; // 格式：host:port,host:port
        private String master = "mymaster";
        private String password;
        private Duration timeout = Duration.ofMillis(5000);
        private int database = 0;
        
        public List<String> getNodeList() {
            if (nodes == null || nodes.trim().isEmpty()) {
                return null;
            }
            return java.util.Arrays.asList(nodes.split(","));
        }
    }
    
    @Data
    public static class Lettuce {
        private Pool pool = new Pool();
        
        @Data
        public static class Pool {
            private boolean enabled = true;
            private int minIdle = 40;
            private int maxIdle = 40;
            private int maxActive = 80;
            private Duration maxWait = Duration.ofSeconds(30);
        }
    }
} 