package com.easyink.common.redis;

import com.easyink.common.core.redis.RedisCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名: 客户redis工具类
 *
 * @author : silver_chariot
 * @date : 2023/6/8 17:34
 **/
@Component("customerRedisCache")
public class CustomerRedisCache extends RedisCache {

    /**
     * 编辑客户回调处理redis key
     */
    private static final String CALL_BACK_EDIT_CUSTOMER_KEY = "callbackEditCustomer:";
    /**
     * 存入redis的userid和externnaluserid之间的间隔
     */
    private static final String VALUE_SEPARATOR = ":";
    private static final int USER_ID_INDEX = 0;
    private static final int EXTERNAL_USER_ID = 1;

    /**
     * 获取 编辑客户回调处理redis key
     *
     * @param corpId 企业id
     * @return
     */
    private String getCallBackEditCustomerKey(String corpId) {
        return CALL_BACK_EDIT_CUSTOMER_KEY + corpId;
    }

    /**
     * 保存需要稍后处理的 回调客户
     *
     * @param corpId         企业id
     * @param userId         员工id
     * @param externalUserId 客户id
     */
    public void saveCallback(String corpId, String userId, String externalUserId) {
        if (StringUtils.isAnyBlank(corpId, externalUserId)) {
            return;
        }
        redisTemplate.opsForHash().put(getCallBackEditCustomerKey(corpId), callbackValue(userId,externalUserId), externalUserId);
    }

    /**
     * 生成存入redis的value
     *
     * @param userId         员工id
     * @param externalUserId 客户id
     * @return redis value
     */
    private String callbackValue(String userId, String externalUserId) {
        return userId + VALUE_SEPARATOR + externalUserId;
    }

    /**
     * 获取回到的客户
     *
     * @param corpId 企业id
     * @return {@link RedisCustomerModel }
     */
    public List<RedisCustomerModel> getCallbackCustomerModel(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        String redisKey = getCallBackEditCustomerKey(corpId);
        List<RedisCustomerModel> list = new ArrayList<>();
        redisTemplate.executePipelined((RedisCallback) callback -> {
            Map<String,String > map = redisTemplate.opsForHash().entries(redisKey);
            if(MapUtils.isEmpty(map)) {
                return null;
            }
            for (Map.Entry<String,String> entry : map.entrySet()) {
                String key =entry.getKey();
                String externalUserId= entry.getValue();
                if (StringUtils.isNoneBlank(key,externalUserId)) {
                    list.add(new RedisCustomerModel(key.split(VALUE_SEPARATOR)[USER_ID_INDEX], externalUserId));
                }
                redisTemplate.opsForHash().delete(redisKey,key);
            }
            return null;
        });
        return list;
    }


    @Data
    @AllArgsConstructor
    public static class RedisCustomerModel {
        /**
         * 员工id
         */
        private String userId;
        /**
         * 客户id
         */
        private String externalUserId;
    }

}
