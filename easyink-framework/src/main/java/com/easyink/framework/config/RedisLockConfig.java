package com.easyink.framework.config;

import com.easyink.common.lock.LockUtil;
import com.easyink.framework.config.properties.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * redis分布式锁配置
 * 支持单机、集群、哨兵模式
 *
 * @author : wangzimo
 * @date : 2021-3-17 20:05:34
 */
@Slf4j
@Configuration
public class RedisLockConfig {

	/**
	 * RedissonClient配置，支持单机、集群、哨兵模式
	 */
	@Bean(destroyMethod = "shutdown")
	@Primary
	public RedissonClient redissonClient(RedisProperties properties) {
		System.out.println("=== Redisson分布式锁配置开始 ===");
		System.out.println("Redisson模式: " + properties.getMode());
		
		Config config = new Config();
		
		switch (properties.getMode().toLowerCase()) {
			case "cluster":
				configCluster(config, properties);
				break;
			case "sentinel":
				configSentinel(config, properties);
				break;
			case "single":
			default:
				configSingle(config, properties);
				break;
		}
		
		RedissonClient client = Redisson.create(config);
		
		// 测试连接并打印连接信息
		testAndLogRedissonConnection(client, properties);
		
		System.out.println("=== Redisson分布式锁配置完成 ===");
		return client;
	}
	
	/**
	 * 配置单机模式
	 */
	private void configSingle(Config config, RedisProperties properties) {
		SingleServerConfig singleServerConfig = config.useSingleServer();
		singleServerConfig.setAddress("redis://" + properties.getHost() + ":" + properties.getPort());
		singleServerConfig.setTimeout((int) properties.getTimeout().toMillis());
		singleServerConfig.setDatabase(properties.getDatabase());
		
		if (StringUtils.isNotBlank(properties.getPassword())) {
			singleServerConfig.setPassword(properties.getPassword());
		}
		
		// 配置连接池
		if (properties.getLettuce().getPool().isEnabled()) {
			singleServerConfig.setConnectionPoolSize(properties.getLettuce().getPool().getMaxActive());
			singleServerConfig.setConnectionMinimumIdleSize(properties.getLettuce().getPool().getMinIdle());
		}
	}
	
	/**
	 * 配置集群模式
	 */
	private void configCluster(Config config, RedisProperties properties) {
		ClusterServersConfig clusterConfig = config.useClusterServers();
		
		// 设置集群节点
		List<String> nodes = properties.getCluster().getNodeList();
		if (nodes != null && !nodes.isEmpty()) {
			for (String node : nodes) {
				clusterConfig.addNodeAddress("redis://" + node);
			}
		}
		
		// 设置密码
		if (StringUtils.isNotBlank(properties.getCluster().getPassword())) {
			clusterConfig.setPassword(properties.getCluster().getPassword());
		}
		
		// 设置超时时间
		clusterConfig.setTimeout((int) properties.getCluster().getTimeout().toMillis());
		
		// 设置重定向次数
		clusterConfig.setRetryAttempts(properties.getCluster().getMaxRedirects());
		
		// 设置刷新周期
		clusterConfig.setScanInterval((int) properties.getCluster().getRefreshPeriod().toMillis());
		
		// 配置连接池
		if (properties.getLettuce().getPool().isEnabled()) {
			clusterConfig.setMasterConnectionPoolSize(properties.getLettuce().getPool().getMaxActive());
			clusterConfig.setSlaveConnectionPoolSize(properties.getLettuce().getPool().getMaxActive());
			clusterConfig.setMasterConnectionMinimumIdleSize(properties.getLettuce().getPool().getMinIdle());
			clusterConfig.setSlaveConnectionMinimumIdleSize(properties.getLettuce().getPool().getMinIdle());
		}
	}
	
	/**
	 * 配置哨兵模式
	 */
	private void configSentinel(Config config, RedisProperties properties) {
		SentinelServersConfig sentinelConfig = config.useSentinelServers();
		
		// 设置哨兵节点（仅使用配置的 nodes，不进行自动发现）
		List<String> nodes = properties.getSentinel().getNodeList();
		if (nodes != null && !nodes.isEmpty()) {
			for (String node : nodes) {
				sentinelConfig.addSentinelAddress("redis://" + node);
			}
		}
		
		// 设置主节点名称
		sentinelConfig.setMasterName(properties.getSentinel().getMaster());
		
		// 设置密码（数据节点密码）
		if (StringUtils.isNotBlank(properties.getSentinel().getPassword())) {
			sentinelConfig.setPassword(properties.getSentinel().getPassword());
		}
		
		// 设置超时时间
		sentinelConfig.setTimeout((int) properties.getSentinel().getTimeout().toMillis());
		
		// 设置数据库
		sentinelConfig.setDatabase(properties.getSentinel().getDatabase());
		
		// 关闭哨兵发现与列表强校验，强制仅使用配置的 nodes
		sentinelConfig.setSentinelsDiscovery(false);
		sentinelConfig.setCheckSentinelsList(false);
		
		// 配置连接池
		if (properties.getLettuce().getPool().isEnabled()) {
			sentinelConfig.setMasterConnectionPoolSize(properties.getLettuce().getPool().getMaxActive());
			sentinelConfig.setSlaveConnectionPoolSize(properties.getLettuce().getPool().getMaxActive());
			sentinelConfig.setMasterConnectionMinimumIdleSize(properties.getLettuce().getPool().getMinIdle());
			sentinelConfig.setSlaveConnectionMinimumIdleSize(properties.getLettuce().getPool().getMinIdle());
		}
	}



	/**
	 * 测试Redisson连接并打印连接信息
	 */
	private void testAndLogRedissonConnection(RedissonClient client, RedisProperties properties) {
		try {
			// 测试连接
			client.getKeys().count();
			
			System.out.println("✅ Redisson连接成功!");
			
			switch (properties.getMode().toLowerCase()) {
				case "single":
					System.out.println("✅ Redisson单机模式连接成功!");
					System.out.println("   主机: " + properties.getHost());
					System.out.println("   端口: " + properties.getPort());
					System.out.println("   数据库: " + properties.getDatabase());
					System.out.println("   密码: " + (properties.getPassword() != null ? "已设置" : "未设置"));
					break;
					
				case "cluster":
					System.out.println("✅ Redisson集群模式连接成功!");
					List<String> clusterNodes = properties.getCluster().getNodeList();
					System.out.println("   集群节点数量: " + (clusterNodes != null ? clusterNodes.size() : 0));
					if (clusterNodes != null) {
						for (int i = 0; i < clusterNodes.size(); i++) {
							System.out.println("   节点" + (i + 1) + ": " + clusterNodes.get(i));
						}
					}
					System.out.println("   集群密码: " + (properties.getCluster().getPassword() != null ? "已设置" : "未设置"));
					System.out.println("   超时时间: " + properties.getCluster().getTimeout().toMillis() + "ms");
					break;
					
				case "sentinel":
					System.out.println("✅ Redisson哨兵模式连接成功!");
					List<String> sentinelNodes = properties.getSentinel().getNodeList();
					System.out.println("   哨兵节点数量: " + (sentinelNodes != null ? sentinelNodes.size() : 0));
					if (sentinelNodes != null) {
						for (int i = 0; i < sentinelNodes.size(); i++) {
							System.out.println("   哨兵" + (i + 1) + ": " + sentinelNodes.get(i));
						}
					}
					System.out.println("   主节点名称: " + properties.getSentinel().getMaster());
					System.out.println("   哨兵密码: " + (properties.getSentinel().getPassword() != null ? "已设置" : "未设置"));
					System.out.println("   数据库: " + properties.getSentinel().getDatabase());
					System.out.println("   超时时间: " + properties.getSentinel().getTimeout().toMillis() + "ms");
					break;
			}
			
			// 打印连接池信息
			System.out.println("   Redisson连接池配置:");
			System.out.println("     最小空闲连接: " + properties.getLettuce().getPool().getMinIdle());
			System.out.println("     最大空闲连接: " + properties.getLettuce().getPool().getMaxIdle());
			System.out.println("     最大活跃连接: " + properties.getLettuce().getPool().getMaxActive());
			System.out.println("     最大等待时间: " + properties.getLettuce().getPool().getMaxWait().toMillis() + "ms");
			
		} catch (Exception e) {
			System.err.println("❌ Redisson连接失败: " + ExceptionUtils.getStackTrace(e));
		}
	}

    @Bean
    public RedissonClient redissonLocker(RedissonClient redissonClient) {
        //设置LockUtil的锁处理对象
        LockUtil.setLocker(redissonClient);
        return redissonClient;
    }
}
