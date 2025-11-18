package com.easyink.wecom.utils;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.wecom.domain.model.mapapi.WeMapConfigModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author tigger
 * 2025/4/28 14:55
 **/

@Component
public class MapConfigRedisCache extends RedisCache {


    public static final String MAP_CONFIG_KEY = "WeMap:Config:";

    /**
     * 地图API调用计数前缀 格式：we:map:count:corpId:mapType:apiKey:date
     */
    public static final String WE_MAP_API_CORP_COUNT_PREFIX = "WeMap:CorpCount:";
    public static final String WE_MAP_API_DEFAULT_COUNT_PREFIX = "WeMap:DefaultCount:";
    /**
     * 省市区接口缓存data key前缀
     */
    public static final String WE_MAP_DISTRICT_LIST_CACHE_PREFIX = "WeMap:DistrictListCache:";

    private String getMapConfigKey(String corpId, Integer mapType) {
        return MAP_CONFIG_KEY + corpId + ":" + mapType;
    }


    public WeMapConfigModel getMapConfigCache(String corpId, Integer mapType) {
        Object cacheObject = getCacheObject(getMapConfigKey(corpId, mapType));
        if (cacheObject != null) {
            return JSON.parseObject(cacheObject.toString(), WeMapConfigModel.class);
        }
        return null;
    }

    /**
     * 设置地图配置缓存
     * @param corpId
     * @param mapType
     * @param configFromDb
     */
    public void setMapConfigCache(String corpId, Integer mapType, WeMapConfigModel configFromDb) {
        setCacheObject(getMapConfigKey(corpId, mapType), JSON.toJSONString(configFromDb), 30, TimeUnit.MINUTES);
    }

    /**
     * 清除地图配置缓存
     * @param corpId 企业id
     * @param mapType 地图类型
     */
    public void clearMapConfigCache(String corpId, Integer mapType) {
        deleteObject(getMapConfigKey(corpId, mapType));
    }


    // ------------- 调用次数

    /**
     * 获取今日计数Key
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param apiCode
     * @return Redis Key
     */
    private String getCorpCountKey(String corpId, Integer mapType, String apiKey, Integer apiCode, Date nowDate) {
        String dateStr = DateUtil.formatDate(nowDate);
        return WE_MAP_API_CORP_COUNT_PREFIX + dateStr + ":" + corpId + ":" + mapType + ":" + apiKey + ":" + apiCode;
    }

    /**
     * 获取默认配置计数Key
     *
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param apiCode
     * @param nowDate 日期
     * @return Redis Key
     */
    private String getDefaultCountKey(Integer mapType, String apiKey, Integer apiCode, Date nowDate) {
        String dateStr = DateUtil.formatDate(nowDate);
        return WE_MAP_API_DEFAULT_COUNT_PREFIX + dateStr + ":" + mapType + ":" + apiKey + ":" + apiCode;
    }


    /**
     * 记录API调用
     * 
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate 日期
     */
    public void recordApiCall(String corpId, Integer mapType, String apiKey, Integer apiCode, Date nowDate) {
        String countKey;
        if (corpId == null) {
            countKey = getDefaultCountKey(mapType, apiKey, apiCode, nowDate);
        } else {
            countKey = getCorpCountKey(corpId, mapType, apiKey, apiCode, nowDate);
        }
        // 增加计数
        this.increment(countKey, 1);

        // 设置过期时间，确保第二天自动清零
        long secondsUntilTomorrow = getSecondsUntilTomorrow();
        if (secondsUntilTomorrow > 0) {
            this.expire(countKey, secondsUntilTomorrow, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取调用次数
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param apiCode
     * @param nowDate 日期
     * @return 调用次数
     */
    public int getCallCount(String corpId, Integer mapType, String apiKey, Integer apiCode, Date nowDate) {
        String key;
        if (StringUtils.isBlank(corpId)) {
            // 总数的key
            key = getDefaultCountKey(mapType, apiKey, apiCode, nowDate);
        } else {
            // 企业的key
            key = getCorpCountKey(corpId, mapType, apiKey,apiCode, nowDate);
        }
        Integer count = this.getCacheObject(key);
        return count == null ? 0 : count;
    }

    /**
     * 获取到明天0点的秒数
     *
     * @return 秒数
     */
    private long getSecondsUntilTomorrow() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        return tomorrow.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toEpochSecond() -
                System.currentTimeMillis() / 1000;
    }

    /**
     * 删除API调用计数
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param code
     * @param today   日期
     */
    public void deleteApiCallCount(String corpId, Integer mapType, String apiKey, Integer apiCode, Date today) {
        String key;
        if (StringUtils.isBlank(corpId)) {
            key = getDefaultCountKey(mapType, apiKey, apiCode, today);
        } else {
            key = getCorpCountKey(corpId, mapType, apiKey, apiCode, today);
        }
        deleteObject(key);
    }

    /**
     * 生成企业API调用计数的Redis键（带接口类型）
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate 日期
     * @param apiCode 接口类型代码
     * @return Redis键
     */
    private String getCorpCountKeyWithApiCode(String corpId, Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        String dateStr = DateUtil.formatDate(nowDate);
        return WE_MAP_API_CORP_COUNT_PREFIX + dateStr + ":" + corpId + ":" + mapType + ":" + apiKey + ":" + apiCode;
    }

    /**
     * 生成默认API调用计数的Redis键（带接口类型）
     *
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate 日期
     * @param apiCode 接口类型代码
     * @return Redis键
     */
    private String getDefaultCountKeyWithApiCode(Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        String dateStr = DateUtil.formatDate(nowDate);
        return WE_MAP_API_DEFAULT_COUNT_PREFIX + dateStr + ":" + mapType + ":" + apiKey + ":" + apiCode;
    }

    /**
     * 记录带接口类型的API调用
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate 日期
     * @param apiCode 接口类型代码
     */
    public void recordApiCallWithApiCode(String corpId, Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        String countKey;
        if (corpId == null) {
            countKey = getDefaultCountKeyWithApiCode(mapType, apiKey, nowDate, apiCode);
        } else {
            countKey = getCorpCountKeyWithApiCode(corpId, mapType, apiKey, nowDate, apiCode);
        }
        // 增加计数
        this.increment(countKey, 1);

        // 设置过期时间，确保第二天自动清零
        long secondsUntilTomorrow = getSecondsUntilTomorrow();
        if (secondsUntilTomorrow > 0) {
            this.expire(countKey, secondsUntilTomorrow, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取带接口类型的调用次数
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate 日期
     * @param apiCode 接口类型代码
     * @return 调用次数
     */
    public int getCallCountWithApiCode(String corpId, Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        String key;
        if (StringUtils.isBlank(corpId)) {
            // 总数的key
            key = getDefaultCountKeyWithApiCode(mapType, apiKey, nowDate, apiCode);
        } else {
            // 企业的key
            key = getCorpCountKeyWithApiCode(corpId, mapType, apiKey, nowDate, apiCode);
        }
        Integer count = this.getCacheObject(key);
        return count == null ? 0 : count;
    }

    /**
     * 删除带接口类型的API调用计数
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param today   日期
     * @param apiCode 接口类型代码
     */
    public void deleteApiCallCountWithApiCode(String corpId, Integer mapType, String apiKey, Date today, Integer apiCode) {
        String key;
        if (StringUtils.isBlank(corpId)) {
            key = getDefaultCountKeyWithApiCode(mapType, apiKey, today, apiCode);
        } else {
            key = getCorpCountKeyWithApiCode(corpId, mapType, apiKey, today, apiCode);
        }
        deleteObject(key);
    }

    // ------------------接口缓存

    /**
     * 获取省市区data缓存key
     * @param mapType
     * @return
     */
    private String getDistrictListApiCachePrefixKey(Integer mapType) {
        return WE_MAP_DISTRICT_LIST_CACHE_PREFIX + mapType;
    }

    /**
     * 获取省市区data数据
     * @param mapType
     * @return
     */
    public JSONObject getDistrictListApiCache(Integer mapType) {
        Object cacheObject = getCacheObject(getDistrictListApiCachePrefixKey(mapType));
        if (cacheObject != null) {
            return JSON.parseObject(cacheObject.toString());
        }
        return null;
    }

    /**
     * 设置获取省市区data数据
     * @param mapType
     * @param data
     */
    public void setDistrictListApiCache(Integer mapType, JSONObject data) {
        setCacheObject(getDistrictListApiCachePrefixKey(mapType), data.toJSONString(), 24, TimeUnit.HOURS);
    }

}
