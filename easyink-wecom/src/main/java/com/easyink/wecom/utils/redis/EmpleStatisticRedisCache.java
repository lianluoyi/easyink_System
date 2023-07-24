package com.easyink.wecom.utils.redis;

import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.redis.RedisEmpleStatisticBaseModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 类名：活码统计Redis工具类
 *
 * @author lichaoyu
 * {@code @date} 2023/7/13 16:51
 */
@Component("empleStatisticRedisCache")
public class EmpleStatisticRedisCache extends RedisCache {

    /**
     * 今日活码新增客户数
     */
    private static final String EMPLE_CODE_TODAY_ADD_CNT = "empleCodeTodayAddCnt:";

    /**
     * 今日活码流失客户数
     */
    private static final String EMPLE_CODE_TODAY_LOSS_CNT = "empleCodeTodayLossCnt:";

    /**
     * 活码维度
     */
    private static final String EMPLE_SCOPE = "empleScope:";

    /**
     * 员工维度
     */
    private static final String USER_SCOPE = "userScope:";

    /**
     * KEY，间隔符号
     */
    private static final String KEY_SEPARATOR = ":";

    /**
     * 接收到回调时，新增的值大小
     */
    private static final int DEFAULT_ADD_VALUE = 1;

    /**
     * 获取活码维度-今日新增客户数REDIS KEY
     *
     * @param corpId 企业ID
     * @param date 日期 格式为YYYY-MM-DD
     * @return KEY
     */
    private String getEmpleScopeAddKey(String corpId, String date, String empleCodeId) {
        return EMPLE_CODE_TODAY_ADD_CNT + EMPLE_SCOPE + corpId + KEY_SEPARATOR + date + KEY_SEPARATOR + empleCodeId;
    }

    /**
     * 获取活码维度-今日流失客户数REDIS KEY
     *
     * @param corpId 企业ID
     * @param date 日期 格式为YYYY-MM-DD
     * @return KEY
     */
    private String getEmpleScopeLossKey(String corpId, String date, String empleCodeId) {
        return EMPLE_CODE_TODAY_LOSS_CNT + EMPLE_SCOPE + corpId + KEY_SEPARATOR + date + KEY_SEPARATOR + empleCodeId;
    }

    /**
     * 获取员工维度-今日新增客户数REDIS KEY
     *
     * @param corpId 企业ID
     * @param date 日期 格式为YYYY-MM-DD
     * @return KEY
     */
    private String getUserScopeAddKey(String corpId, String date, String userId) {
        return EMPLE_CODE_TODAY_ADD_CNT + USER_SCOPE + corpId + KEY_SEPARATOR + date + KEY_SEPARATOR + userId;
    }

    /**
     * 获取员工维度-今日流失客户数REDIS KEY
     *
     * @param corpId 企业ID
     * @param date 日期 格式为YYYY-MM-DD
     * @return KEY
     */
    private String getUserScopeLossKey(String corpId, String date, String userId) {
        return EMPLE_CODE_TODAY_LOSS_CNT + USER_SCOPE + corpId + KEY_SEPARATOR + date + KEY_SEPARATOR + userId;
    }

    /**
     * 新增回调-新增员工维度和活码维度的新增客户数
     *
     * @param corpId 企业ID
     * @param date 日期，格式为YYYY-MM-DD
     * @param empleCodeId 活码ID
     * @param userId 员工ID
     */
    public void addNewCustomerCnt(String corpId, String date, Long empleCodeId, String userId) {
        if (StringUtils.isAnyBlank(corpId, date, userId) || empleCodeId == null) {
            return;
        }
        // 活码维度-新增客户数Key
        String empleScopeAddKey = getEmpleScopeAddKey(corpId, date, String.valueOf(empleCodeId));
        // 员工维度-新增客户数Key
        String userScopeAddKey = getUserScopeAddKey(corpId, date, userId);
        // 管道操作，批量保存
        empleRedisTemplate.executePipelined((RedisCallback) callback -> {
                // 活码维度-新增客户数 + 1
                increment(empleScopeAddKey, userId, 1);
                // 员工维度-新增客户数 + 1
                increment(userScopeAddKey, empleCodeId, 1);
            // 结束管道操作
            return null;
        });
    }

    /**
     * 新增回调-新增员工维度和活码维度的流失客户数
     *
     * @param corpId 企业ID
     * @param date 日期，格式为YYYY-MM-DD
     * @param empleCodeId 活码ID
     * @param userId 员工ID
     */
    public void addLossCustomerCnt(String corpId, String date, Long empleCodeId, String userId) {
        if (StringUtils.isAnyBlank(corpId, date, userId) || empleCodeId == null) {
            return;
        }
        // 活码维度-流失客户数Key
        String empleScopeLossKey = getEmpleScopeLossKey(corpId, date, String.valueOf(empleCodeId));
        // 员工维度-流失客户数Key
        String userScopeLossKey = getUserScopeLossKey(corpId, date, userId);
        // 管道操作，批量添加
        empleRedisTemplate.executePipelined((RedisCallback) callback -> {
                // 活码维度-流失客户数 + 1
                increment(empleScopeLossKey, userId, 1);
                // 员工维度-流失客户数 + 1
                increment(userScopeLossKey, empleCodeId, 1);
            // 结束管道操作
            return null;
        });
    }

    /**
     * 获取Redis中活码维度的新增客户数/流失客户数
     *
     * @param corpId 企业ID
     * @param date 日期，格式为YYYY-MM-DD
     * @param empleCodeIdList 活码ID列表
     * @return Map Key: 活码ID， Value: {@link RedisEmpleStatisticBaseModel}
     */
    public List<WeEmpleCodeStatistic> getBatchEmpleValue(String corpId, String date, List<Long> empleCodeIdList, List<String> userIdList) {
        if (StringUtils.isAnyBlank(corpId, date) || CollectionUtils.isEmpty(empleCodeIdList) || CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        // 返回的列表
        List<WeEmpleCodeStatistic> resultList = new ArrayList<>();
        // 管道操作，批量获取
        empleRedisTemplate.executePipelined((RedisCallback) callback -> {
            for (Long empleCodeId : empleCodeIdList) {
                // 活码维度-新增客户数Key
                String empleScopeAddKey = getEmpleScopeAddKey(corpId, date, String.valueOf(empleCodeId));
                // 活码维度-流失客户数Key
                String empleScopeLossKey = getEmpleScopeLossKey(corpId, date, String.valueOf(empleCodeId));
                for (String userId : userIdList) {
                    // 获取活码维度新增客户数
                    int newCnt = getHashIncrCnt(empleScopeAddKey, userId);
                    // 获取活码维度流失客户数
                    int lossCnt = getHashIncrCnt(empleScopeLossKey, userId);
                    // 组装数据
                    WeEmpleCodeStatistic weEmpleCodeStatistic = new WeEmpleCodeStatistic();
                    weEmpleCodeStatistic.setEmpleCodeId(empleCodeId);
                    weEmpleCodeStatistic.setUserId(userId);
                    weEmpleCodeStatistic.setNewCustomerCnt(newCnt);
                    weEmpleCodeStatistic.setLossCustomerCnt(lossCnt);
                    resultList.add(weEmpleCodeStatistic);
                }
            }
            // 结束管道操作
            return null;
        });
        return resultList;
    }

    /**
     * 获取Redis中员工维度的新增客户数/流失客户数
     *
     * @param corpId 企业ID
     * @param date 日期，格式为YYYY-MM-DD
     * @param userIdList 员工ID列表
     * @return Map Key: 员工ID， Value: {@link RedisEmpleStatisticBaseModel}
     */
    public List<WeEmpleCodeStatistic> getBatchUserValue(String corpId, String date, List<String> userIdList, List<Long> empleCodeIdList) {
        if (StringUtils.isAnyBlank(corpId, date) || CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        // 返回的列表
        List<WeEmpleCodeStatistic> resultList = new ArrayList<>();
        // 管道操作，批量获取
        redisTemplate.executePipelined((RedisCallback) callback -> {
            for (String userId : userIdList) {
                // 员工维度-新增客户数Key
                String userScopeAddKey = getUserScopeAddKey(corpId, date, userId);
                // 员工维度-流失客户数Key
                String userScopeLossKey = getUserScopeLossKey(corpId, date, userId);
                for (Long empleCodeId : empleCodeIdList) {
                    // 获取员工维度新增客户数
                    int newCnt = getHashIncrCnt(userScopeAddKey, empleCodeId);
                    // 获取员工维度流失客户数
                    int lossCnt = getHashIncrCnt(userScopeLossKey, empleCodeId);
                    // 组装数据
                    WeEmpleCodeStatistic weEmpleCodeStatistic = new WeEmpleCodeStatistic();
                    weEmpleCodeStatistic.setUserId(userId);
                    weEmpleCodeStatistic.setEmpleCodeId(empleCodeId);
                    weEmpleCodeStatistic.setNewCustomerCnt(newCnt);
                    weEmpleCodeStatistic.setLossCustomerCnt(lossCnt);
                    resultList.add(weEmpleCodeStatistic);
                }
            }
            // 结束管道操作
            return null;
        });
        return resultList;
    }

    /**
     * 获取Redis中日期维度的新增客户数/流失客户数
     *
     * @param corpId 企业ID
     * @param date 日期，格式为YYYY-MM-DD
     * @param empleCodeIdList 活码ID列表
     * @return {@link RedisEmpleStatisticBaseModel}
     */
    public RedisEmpleStatisticBaseModel getBatchDateValue(String corpId, String date, List<Long> empleCodeIdList, List<String> userIdList) {
        if (StringUtils.isAnyBlank(corpId, date) || CollectionUtils.isEmpty(empleCodeIdList) || CollectionUtils.isEmpty(userIdList)) {
            return null;
        }
        // 活码维度数据
        List<WeEmpleCodeStatistic> empleMap = getBatchEmpleValue(corpId, date, empleCodeIdList, userIdList);
        int addCnt = 0;
        int lossCnt = 0;
        for (WeEmpleCodeStatistic data : empleMap) {
            for (Long empleCodeId : empleCodeIdList) {
                for (String userId : userIdList) {
                    if (data.getUserId().equals(userId) && data.getEmpleCodeId().equals(empleCodeId)) {
                        addCnt += data.getNewCustomerCnt();
                        lossCnt += data.getLossCustomerCnt();
                    }
                }
            }
        }
        return RedisEmpleStatisticBaseModel.builder().newCustomerCnt(addCnt).lossCustomerCnt(lossCnt).build();
    }

    /**
     * 根据日期批量删除Key
     *
     * @param corpId 企业ID
     * @param date 日期，格式为YYYY-MM-DD
     */
    public void batchRemoveByDate(String corpId, String date, List<Long> empleCodeIdList, List<String> userIdList) {
        if (StringUtils.isAnyBlank(corpId, date)) {
            return;
        }
        // 批量删除员工维度，活码维度key
        redisTemplate.executePipelined((RedisCallback) callback -> {
            for (Long empleCodeId : empleCodeIdList) {
                // 活码维度-新增客户数Key
                String empleScopeAddKey = getEmpleScopeAddKey(corpId, date, String.valueOf(empleCodeId));
                // 活码维度-流失客户数Key
                String empleScopeLossKey = getEmpleScopeLossKey(corpId, date, String.valueOf(empleCodeId));
                // 删除活码维度新增客户数/流失客户数
                redisTemplate.delete(empleScopeAddKey);
                redisTemplate.delete(empleScopeLossKey);
            }
            for (String userId : userIdList) {
                // 员工维度-新增客户数Key
                String userScopeAddKey = getUserScopeAddKey(corpId, date, userId);
                // 员工维度-流失客户数Key
                String userScopeLossKey = getUserScopeLossKey(corpId, date, userId);
                // 删除员工维度新增客户数/流失客户数
                redisTemplate.delete(userScopeAddKey);
                redisTemplate.delete(userScopeLossKey);
            }
            return null;
        });
    }

}
