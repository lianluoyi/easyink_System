package com.easyink.wecom.openapi.util;


import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.openapi.dao.AppIdInfoMapper;
import com.easyink.wecom.openapi.domain.entity.AppIdInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类名: appId和appSecret本地缓存
 *
 * @author : silver_chariot
 * @date : 2022/3/14 14:24
 */
@Slf4j
@Component
public class AppIdCache {

    /**
     * app开发设置缓存
     */
    private static final ConcurrentHashMap<String, AppIdInfo> CACHE = new ConcurrentHashMap<>();


    /**
     * 从db初始化app配置缓存,在系统启用时调用
     * {@link AppIdInfo}
     */
    @PostConstruct
    public void init() {
        log.info("[OpenApi开发参数缓存]初始化开始...");
        AppIdInfoMapper appIdInfoMapper = SpringUtils.getBean("appIdInfoMapper");
        List<AppIdInfo> list = appIdInfoMapper.getAll();
        if (CollectionUtils.isEmpty(list)) {
            log.info("[OpenApi开发参数缓存]初始化结束,系统无可用开发配置...");
            return;
        }
        list.forEach(appIdInfo -> CACHE.put(appIdInfo.getAppId(), appIdInfo));
        log.info("[OpenApi开发参数缓存]初始化结束,共{}个开发配置...", list.size());
    }

    /**
     * 增加缓存
     *
     * @param appId     appId
     * @param appIdInfo {@link AppIdInfo}
     */
    public void put(String appId, AppIdInfo appIdInfo) {
        CACHE.put(appId, appIdInfo);
    }

    /**
     * 获取app缓存
     *
     * @param appId appId
     * @return appIdInfo {@link AppIdInfo}
     */
    public AppIdInfo get(String appId) {
        return CACHE.get(appId);
    }

}
