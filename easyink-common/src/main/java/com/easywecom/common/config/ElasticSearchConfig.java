package com.easywecom.common.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @description ElasticSearch 配置
 * @date 2020/12/4 9:59
 **/

@Data
@Configuration
@ConditionalOnProperty(value = "elasticsearch.open",matchIfMissing = false)
@ConfigurationProperties(value = "elasticsearch")
public class ElasticSearchConfig {
    /**
     * 协议
     */
    private String schema = "http";
    /**
     * 集群地址，如果有多个用“,”隔开
     */
    private String address;

    private String userName;

    private String password;
    /**
     * 连接超时时间
     */
    private int connectTimeout = 5000;

    /**
     * Socket 连接超时时间
     */
    private int socketTimeout = 10000;

    /**
     * 获取连接的超时时间
     */
    private int connectionRequestTimeout = 5000;

    /**
     * 最大连接数
     */
    private int maxConnectNum = 100;

    /**
     * 最大路由连接数
     */
    private int maxConnectPerRoute = 100;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 拆分地址
        List<HttpHost> hostLists = new ArrayList<>();
        String[] hostList = address.split(",");
        for (String addr : hostList) {
            String host = addr.split(":")[0];
            String port = addr.split(":")[1];
            hostLists.add(new HttpHost(host, Integer.parseInt(port), schema));
        }
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        // 转换成 HttpHost 数组
        HttpHost[] httpHost = hostLists.toArray(new HttpHost[]{});
        // 构建连接对象
        RestClientBuilder builder = RestClient.builder(httpHost);
        // 异步连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            return requestConfigBuilder;
        });
        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            return httpClientBuilder;
        });
        return new RestHighLevelClient(builder);

    }
}