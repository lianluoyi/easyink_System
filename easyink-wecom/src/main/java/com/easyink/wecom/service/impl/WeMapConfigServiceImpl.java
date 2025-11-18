package com.easyink.wecom.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.map.WeMapConfigDTO;
import com.easyink.wecom.domain.entity.WeMapConfig;
import com.easyink.wecom.domain.enums.MapTypeEnum;
import com.easyink.wecom.domain.enums.mapapi.MapApiCodeEnum;
import com.easyink.wecom.domain.model.mapapi.ApiLimitInfo;
import com.easyink.wecom.domain.model.mapapi.WeMapConfigModel;
import com.easyink.wecom.domain.vo.WeMapConfigVO;
import com.easyink.wecom.mapper.WeCorpAccountMapper;
import com.easyink.wecom.mapper.WeMapConfigMapper;
import com.easyink.wecom.service.WeMapConfigService;
import com.easyink.wecom.utils.MapConfigRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 地图API配置服务实现类
 *
 * @author wx
 * @date 2023/8/3
 */
@Service
@Slf4j
public class WeMapConfigServiceImpl implements WeMapConfigService {

    private final WeMapConfigMapper weMapConfigMapper;
    private final MapConfigRedisCache mapConfigRedisCache;

    @Autowired
    public WeMapConfigServiceImpl(WeMapConfigMapper weMapConfigMapper,
                                  MapConfigRedisCache mapConfigRedisCache
    ) {
        this.weMapConfigMapper = weMapConfigMapper;
        this.mapConfigRedisCache = mapConfigRedisCache;
    }

    /**
     * 获取系统默认的地图API配置
     *
     * @param mapType 地图类型
     * @return 地图API配置
     */
    @Override
    public WeMapConfig getDefaultConfig(Integer mapType) {
        return weMapConfigMapper.getDefaultConfig(mapType);
    }

    /**
     * 获取企业地图API配置
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @return 配置信息
     */
    @Override
    public WeMapConfig getCorpConfig(String corpId, Integer mapType) {
        return weMapConfigMapper.getCorpConfig(corpId, mapType);
    }

    /**
     * 获取企业地图API配置视图
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @return 配置视图
     */
    @Override
    public WeMapConfigVO getConfigVO(String corpId, Integer mapType) {
        if (mapType == null) {
            throw new CustomException("地图类型不能为空");
        }

        if (StringUtils.isEmpty(corpId)) {
            throw new CustomException("企业id不能未空");
        }
        WeMapConfigVO vo = new WeMapConfigVO();
        // 获取企业配置
        WeMapConfig corpConfig = getCorpConfig(corpId, mapType);

        MapTypeEnum typeEnum = MapTypeEnum.getByCode(mapType);

        if (corpConfig != null) {
            // 使用企业配置
            vo.setHasCorpConfig(true);
            vo.setApiKey(corpConfig.getApiKey());
            vo.setIframeApiKey(corpConfig.getIframeApiKey());
            vo.setMapType(corpConfig.getMapType());
            vo.setMapTypeName(typeEnum.getName());
            List<ApiLimitInfo> apiLimitInfoList = JSON.parseArray(corpConfig.getDailyLimit(), ApiLimitInfo.class);
            List<WeMapConfigVO.DailyLimitInfo> dailyLimits = new ArrayList<>();
            for (ApiLimitInfo apiLimitInfo : apiLimitInfoList) {
                // 获取企业今日使用系统配置的调用次数
                // 获取今日调用次数
                int callCount = getDailyCallCountWithCorp(corpId, mapType, corpConfig.getApiKey(), apiLimitInfo.getApiCode(), DateUtils.getNowDate());
                dailyLimits.add(new WeMapConfigVO.DailyLimitInfo(apiLimitInfo.getApiCode(), apiLimitInfo.getDailyLimit(), callCount));
            }
            vo.setDailyLimits(dailyLimits);
            vo.setStatus(corpConfig.getStatus());
        } else {
            // 使用系统配置
            vo.setHasCorpConfig(false);
            WeMapConfig sysConfig = getDefaultConfig(mapType);
            if (sysConfig != null) {
                vo.setApiKey(sysConfig.getApiKey());
                vo.setIframeApiKey(sysConfig.getIframeApiKey());
                vo.setMapType(sysConfig.getMapType());
                vo.setMapTypeName(typeEnum.getName());
                List<ApiLimitInfo> apiLimitInfoList = JSON.parseArray(sysConfig.getDailyLimit(), ApiLimitInfo.class);
                List<WeMapConfigVO.DailyLimitInfo> dailyLimits = new ArrayList<>();
                for (ApiLimitInfo apiLimitInfo : apiLimitInfoList) {
                    // 获取企业今日使用系统配置的调用次数
                    int callCount = getDailyCallCountWithDefault(mapType, sysConfig.getApiKey(), apiLimitInfo.getApiCode(), DateUtils.getNowDate());
                    dailyLimits.add(new WeMapConfigVO.DailyLimitInfo(apiLimitInfo.getApiCode(), apiLimitInfo.getDailyLimit(), callCount));
                }
                vo.setDailyLimits(dailyLimits);
                vo.setStatus(sysConfig.getStatus());
            }
        }

        return vo;
    }

    /**
     * 保存或更新系统默认地图API配置
     *
     * @param configDTO 配置信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateDefaultConfig(WeMapConfigDTO configDTO) {
        // 查询是否已存在
        WeMapConfig existConfig = getDefaultConfig(configDTO.getMapType());

        WeMapConfig config = new WeMapConfig();
        BeanUtils.copyProperties(configDTO, config);
        config.setDailyLimit(JSON.toJSONString(configDTO.getDailyLimits()));
        config.setCorpId(""); // 系统默认配置

        // 处理敏感字段加密
        SensitiveFieldProcessor.processForSave(config);

        boolean result;
        if (existConfig != null) {
            // 更新
            config.setId(existConfig.getId());
            result = weMapConfigMapper.updateById(config) > 0;
        } else {
            // 新增
            result = weMapConfigMapper.insert(config) > 0;
        }
        // 清除缓存
        clearMapKeyCache(null);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateCorpConfig(WeMapConfigDTO configDTO) {

        if (StringUtils.isBlank(configDTO.getCorpId())) {
            throw new CustomException("企业id不能未空");
        }

        // 查询是否已存在
        WeMapConfig existConfig = getCorpConfig(configDTO.getCorpId(), configDTO.getMapType());

        WeMapConfig config = new WeMapConfig();
        BeanUtils.copyProperties(configDTO, config);
        config.setDailyLimit(JSON.toJSONString(configDTO.getDailyLimits()));
        
        // 处理敏感字段加密
        SensitiveFieldProcessor.processForSave(config);
        
        boolean result;
        if (existConfig != null) {
            // 更新
            config.setId(existConfig.getId());
            result = weMapConfigMapper.updateById(config) > 0;
        } else {
            // 新增
            result = weMapConfigMapper.insert(config) > 0;
        }

        // 清除缓存
        clearMapKeyCache(configDTO.getCorpId());

        return result;
    }

    /**
     * 删除企业地图API配置
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCorpConfig(String corpId, Integer mapType) {
        if (mapType == null) {
            throw new CustomException("地图类型不能为空");
        }

        if (StringUtils.isEmpty(corpId)) {
            throw new CustomException("企业id不能未空");
        }

        WeMapConfig weMapConfig = weMapConfigMapper.selectOne(new LambdaQueryWrapper<WeMapConfig>()
                .eq(WeMapConfig::getCorpId, corpId)
                .eq(WeMapConfig::getMapType, mapType)
        );
        if (weMapConfig == null) {
            throw new CustomException("企业配置不存在");
        }

        LambdaUpdateWrapper<WeMapConfig> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WeMapConfig::getCorpId, corpId)
                .eq(WeMapConfig::getMapType, mapType);

        boolean result = weMapConfigMapper.delete(wrapper) > 0;

        // 清除缓存
        clearMapKeyCache(corpId);

        return result;
    }

    /**
     * 获取地图API密钥配置
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @return API密钥信息
     */
    @Override
    public WeMapConfigModel getMapConfig(String corpId, Integer mapType) {
        if (StringUtils.isBlank(corpId) || mapType == null) {
            log.error("[地图API]获取密钥失败，企业ID或地图类型为空");
            return null;
        }

        // 缓存中获取
        WeMapConfigModel cache = mapConfigRedisCache.getMapConfigCache(corpId, mapType);
        if (cache != null) {
            return cache;
        }

        // 从数据库获取
        WeMapConfigModel configFromDb = getConfigFromDb(corpId, mapType);
        if (configFromDb != null) {
            mapConfigRedisCache.setMapConfigCache(corpId, mapType, configFromDb);
        }
        return configFromDb;
    }

    /**
     * 检查是否超过API调用限制
     *
     * @param corpId     企业ID
     * @param mapType    地图类型
     * @param apiKey     API密钥
     * @param dailyLimit 每日限制次数
     * @param nowDate
     * @return 是否超过限制
     */
    @Override
    public boolean isExceedLimit(String corpId, Integer mapType, String apiKey, Integer dailyLimit, Date nowDate) {
        // 如果无限制，直接返回false
        if (dailyLimit == null || dailyLimit < 0 || nowDate == null) {
            return false;
        }
        int callCount;
        if (corpId == null) {
            callCount = getDailyCallCountWithDefault(mapType, apiKey, MapApiCodeEnum.DISTRICT_LIST.getCode(), nowDate);
        } else {
            // 获取今日调用次数
            callCount = getDailyCallCountWithCorp(corpId, mapType, apiKey, MapApiCodeEnum.DISTRICT_LIST.getCode(), nowDate);
        }

        return callCount >= dailyLimit;
    }

    @Override
    public boolean isExceedLimit(Integer mapType, String apiKey, Integer dailyLimit, Date nowDate) {
        return isExceedLimit(null, mapType, apiKey, dailyLimit, nowDate);
    }

    /**
     * 检查是否超过API调用限制（带接口类型）
     *
     * @param corpId     企业ID
     * @param mapType    地图类型
     * @param apiKey     API密钥
     * @param dailyLimit 每日限制次数
     * @param nowDate    当前日期
     * @param apiCode    接口类型代码
     * @return 是否超过限制
     */
    @Override
    public boolean isExceedLimit(String corpId, Integer mapType, String apiKey, Integer dailyLimit, Date nowDate, Integer apiCode) {
        int count = mapConfigRedisCache.getCallCountWithApiCode(corpId, mapType, apiKey, nowDate, apiCode);
        return count >= dailyLimit;
    }

    /**
     * 检查是否超过API调用限制（默认配置，带接口类型）
     *
     * @param mapType    地图类型
     * @param apiKey     API密钥
     * @param dailyLimit 每日限制次数
     * @param nowDate    当前日期
     * @param apiCode    接口类型代码
     * @return 是否超过限制
     */
    @Override
    public boolean isExceedLimit(Integer mapType, String apiKey, Integer dailyLimit, Date nowDate, Integer apiCode) {
        int count = mapConfigRedisCache.getCallCountWithApiCode(null, mapType, apiKey, nowDate, apiCode);
        return count >= dailyLimit;
    }

    /**
     * 记录API调用并增加计数
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate
     */
    @Override
    public void recordApiCall(String corpId, Integer mapType, String apiKey, Date nowDate) {

        try {
            mapConfigRedisCache.recordApiCall(corpId, mapType, apiKey, MapApiCodeEnum.DISTRICT_LIST.getCode(), nowDate);
        } catch (Exception e) {
            log.error("[地图API]记录API调用异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public void recordApiCall(Integer mapType, String apiKey, Date nowDate) {
        recordApiCall(null, mapType, apiKey, nowDate);
    }

    /**
     * 记录API调用并增加计数（带接口类型）
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate 当前日期
     * @param apiCode 接口类型代码
     */
    @Override
    public void recordApiCall(String corpId, Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        mapConfigRedisCache.recordApiCallWithApiCode(corpId, mapType, apiKey, nowDate, apiCode);
    }

    /**
     * 记录API调用并增加计数（默认配置，带接口类型）
     *
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate 当前日期
     * @param apiCode 接口类型代码
     */
    @Override
    public void recordApiCall(Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        mapConfigRedisCache.recordApiCallWithApiCode(null, mapType, apiKey, nowDate, apiCode);
    }

    /**
     * 清除地图API调用次数缓存
     *
     * @param corpId 企业ID
     */
    @Override
    public void clearMapKeyCache(String corpId) {
        // 获取当前日期
        try {
            if (StringUtils.isBlank(corpId)) {
                // 清除没有企业配置的所有企业的缓存
                // 1.查询所有企业id列表
                Set<String> totalCorpIdSet = SpringUtil.getBean(WeCorpAccountMapper.class).selectList(null).stream().map(WeCorpAccount::getCorpId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                // 2.查询有配置地图apikey的企业id
                Set<String> configuredCorpIdList = this.weMapConfigMapper.selectList(null).stream().map(WeMapConfig::getCorpId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                // 3.需要清除的企业id列表
                totalCorpIdSet.removeAll(configuredCorpIdList);
                for (String removeCorpId : totalCorpIdSet) {
                    mapConfigRedisCache.clearMapConfigCache(removeCorpId, MapTypeEnum.TENCENT.getCode());
                }

            } else {
                // 清除企业缓存
                mapConfigRedisCache.clearMapConfigCache(corpId, MapTypeEnum.TENCENT.getCode());
            }
            log.info("[地图API]已清除企业({})的API缓存", corpId);
        } catch (Exception e) {
            log.error("[地图API]清除缓存异常: {}", ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 获取企业API今日调用次数
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param apiCode
     * @return 调用次数
     */
    private int getDailyCallCountWithCorp(String corpId, Integer mapType, String apiKey, Integer apiCode, Date nowDate) {
        return mapConfigRedisCache.getCallCount(corpId, mapType, apiKey, apiCode, nowDate);
    }


    /**
     * 获取默认API今日调用次数
     *
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @return 调用次数
     */
    private int getDailyCallCountWithDefault(Integer mapType, String apiKey, Integer apiCode, Date nowDate) {
        return mapConfigRedisCache.getCallCount(null, mapType, apiKey, apiCode, nowDate);
    }


    /**
     * 从数据库获取配置信息
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @return 配置信息
     */
    private WeMapConfigModel getConfigFromDb(String corpId, Integer mapType) {
        try {
            // 首先尝试获取企业配置
            WeMapConfig corpConfig = getCorpConfig(corpId, mapType);
            if (corpConfig != null && StringUtils.isNotEmpty(corpConfig.getApiKey()) && corpConfig.getStatus() == 1) {
                // 检查是否超过调用限制
                return new WeMapConfigModel(corpConfig);
            }

            // 如果企业配置不可用，使用系统配置
            WeMapConfig sysConfig = getDefaultConfig(mapType);
            if (sysConfig != null && StringUtils.isNotEmpty(sysConfig.getApiKey()) && sysConfig.getStatus() == 1) {
                // 检查是否超过调用限制
                return new WeMapConfigModel(sysConfig);
            }

            log.error("[地图API]未找到可用的地图API密钥，企业ID: {}, 地图类型: {}", corpId, mapType);
            return null;
        } catch (Exception e) {
            log.error("[地图API]获取密钥异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成企业API调用计数的Redis键（不带接口类型，保留原有方法）
     */
    private String getCorpCountKey(String corpId, Integer mapType, String apiKey, Date nowDate) {
        String dateStr = DateUtil.formatDate(nowDate);
        return MapConfigRedisCache.WE_MAP_API_CORP_COUNT_PREFIX + dateStr + corpId + ":" + mapType + ":" + apiKey;
    }

    /**
     * 生成默认API调用计数的Redis键（不带接口类型，保留原有方法）
     */
    private String getDefaultCountKey(Integer mapType, String apiKey, Date nowDate) {
        String dateStr = DateUtil.formatDate(nowDate);
        return MapConfigRedisCache.WE_MAP_API_DEFAULT_COUNT_PREFIX + dateStr + mapType + ":" + apiKey;
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
    private String getCorpCountKey(String corpId, Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        String dateStr = DateUtil.formatDate(nowDate);
        return MapConfigRedisCache.WE_MAP_API_CORP_COUNT_PREFIX + dateStr + corpId + ":" + mapType + ":" + apiKey + ":" + apiCode;
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
    private String getDefaultCountKey(Integer mapType, String apiKey, Date nowDate, Integer apiCode) {
        String dateStr = DateUtil.formatDate(nowDate);
        return MapConfigRedisCache.WE_MAP_API_DEFAULT_COUNT_PREFIX + dateStr + mapType + ":" + apiKey + ":" + apiCode;
    }

}