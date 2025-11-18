package com.easyink.wecom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.MapClient;
import com.easyink.wecom.domain.dto.map.DistrictChildrenDTO;
import com.easyink.wecom.domain.dto.map.DistrictListDTO;
import com.easyink.wecom.domain.enums.MapTypeEnum;
import com.easyink.wecom.domain.enums.mapapi.MapApiCodeEnum;
import com.easyink.wecom.domain.model.mapapi.ApiLimitInfo;
import com.easyink.wecom.domain.model.mapapi.WeMapConfigModel;
import com.easyink.wecom.service.MapApiService;
import com.easyink.wecom.service.WeMapConfigService;
import com.easyink.wecom.utils.MapConfigRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 地图服务实现类
 *
 * @author wx
 * @date 2023/8/8
 */
@Service
@Slf4j
public class MapApiServiceImpl implements MapApiService {

    private final MapClient mapClient;
    private final WeMapConfigService mapConfigService;
    private final MapConfigRedisCache mapConfigRedisCache;

    @Autowired
    public MapApiServiceImpl(MapClient mapClient, WeMapConfigService mapConfigService, MapConfigRedisCache mapConfigRedisCache) {
        this.mapClient = mapClient;
        this.mapConfigService = mapConfigService;
        this.mapConfigRedisCache = mapConfigRedisCache;
    }

    /**
     * 获取省市区列表
     *
     * @param districtListDTO 请求参数
     * @return 省市区列表
     */
    @Override
    public JSONObject getDistrictList(DistrictListDTO districtListDTO) {
        log.info("[地图API]获取省市区列表请求参数: {}", districtListDTO);
        if (StringUtils.isEmpty(districtListDTO.getCorpId())) {
            throw new CustomException("企业ID不能为空");
        }

        // 做缓存
        return this.getDistrictListData(districtListDTO);
    }

    /**
     * 获取下级行政区划
     *
     * @param districtChildrenDTO 请求参数
     * @return 下级行政区划列表
     */
    @Override
    public JSONObject getDistrictChildren(DistrictChildrenDTO districtChildrenDTO) {
        log.info("[地图API]获取下级行政区划请求参数: {}", districtChildrenDTO);
        if (StringUtils.isEmpty(districtChildrenDTO.getCorpId())) {
            throw new CustomException("企业ID不能为空");
        }

        // 检查API限制
        checkApiLimit(districtChildrenDTO.getCorpId(), MapTypeEnum.TENCENT.getCode(), MapApiCodeEnum.DISTRICT_CHILDREN.getCode());

        try {
            return mapClient.getDistrictChildren(districtChildrenDTO);
        } catch (ForestRuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof CustomException) {
                throw (CustomException) e.getCause();
            }
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 地址解析（地址转坐标），不指定地址所在城市
     *
     * @param address 地址，标准地址
     * @param corpId  企业ID
     * @return 腾讯地图地址解析API响应
     */
    @Override
    public JSONObject geocoder(String address, String corpId) {
        // 检查参数
        if (StringUtils.isEmpty(address)) {
            throw new CustomException("地址不能为空");
        }
        if (StringUtils.isEmpty(corpId)) {
            throw new CustomException("企业ID不能为空");
        }

        // 检查API限制
//        checkApiLimit(corpId, MapTypeEnum.TENCENT.getCode(), MapApiCodeEnum.GEOCODER.getCode());

        try {
            return mapClient.geocoder(address, corpId);
        } catch (ForestRuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof CustomException) {
                throw (CustomException) e.getCause();
            }
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 逆地址解析（坐标转地址）
     *
     * @param location 位置坐标，格式：lat,lng
     * @param corpId   企业ID
     * @return 腾讯地图逆地址解析API响应
     */
    @Override
    public JSONObject reGeocode(String location, String corpId) {
        // 检查参数
        if (StringUtils.isEmpty(location)) {
            throw new CustomException("位置坐标不能为空");
        }
        if (StringUtils.isEmpty(corpId)) {
            throw new CustomException("企业ID不能为空");
        }

        // 检查API限制
        checkApiLimit(corpId, MapTypeEnum.TENCENT.getCode(), MapApiCodeEnum.RE_GEOCODE.getCode());

        try {
            return mapClient.reGeocode(location, corpId);
        } catch (ForestRuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof CustomException) {
                throw (CustomException) e.getCause();
            }
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 关键词输入提示
     *
     * @param keyword   关键词，如：北京大学
     * @param location  限制城市范围，如：北京
     * @param corpId    企业ID
     * @param pageIndex
     * @param pageSize
     * @return 腾讯地图关键词输入提示API响应
     */
    @Override
    public JSONObject suggestion(String keyword, String location, String corpId, String pageIndex, String pageSize) {
        // 检查参数
        if (StringUtils.isEmpty(keyword)) {
            throw new CustomException("关键词不能为空");
        }
        if (StringUtils.isEmpty(corpId)) {
            throw new CustomException("企业ID不能为空");
        }

        // 检查API限制
        checkApiLimit(corpId, MapTypeEnum.TENCENT.getCode(), MapApiCodeEnum.SUGGESTION.getCode());

        try {
            return mapClient.suggestion(keyword, location, corpId, pageIndex, pageSize);
        } catch (ForestRuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof CustomException) {
                throw (CustomException) e.getCause();
            }
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public JSONObject getDistrictListData(DistrictListDTO districtListDTO) {

        // 缓存中获取
        JSONObject cache = mapConfigRedisCache.getDistrictListApiCache(MapTypeEnum.TENCENT.getCode());
        if (cache != null) {
            return cache;
        }


        // 检查API限制
        checkApiLimit(districtListDTO.getCorpId(), MapTypeEnum.TENCENT.getCode(), MapApiCodeEnum.DISTRICT_LIST.getCode());
        try {
            log.info("[地图API] 请求api接口获取行政区");
            // 发起请求查询一次
            JSONObject districtList = mapClient.getDistrictList(districtListDTO);
            mapConfigRedisCache.setDistrictListApiCache(MapTypeEnum.TENCENT.getCode(), districtList);
            return districtList;
        } catch (ForestRuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof CustomException) {
                throw (CustomException) e.getCause();
            }
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 检查API调用限制并处理API密钥
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiCode 接口类型代码，来自MapApiCodeEnum
     */
    public void checkApiLimit(String corpId, Integer mapType, Integer apiCode) {
        if (StringUtils.isBlank(corpId) || mapType == null || apiCode == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 获取API密钥配置
        WeMapConfigModel mapConfig = mapConfigService.getMapConfig(corpId, mapType);

        if (mapConfig == null || StringUtils.isBlank(mapConfig.getApiKey())) {
            log.info("[地图API] 不存在可用的地图配置, 企业ID: {}, mapType: {}",
                    corpId, mapType);
            throw new CustomException("请联系运维人员配置地图api");
        }

        // 查找对应API的限制, 默认没配置则无限制
        Integer limitValue = -1;
        if (CollectionUtils.isNotEmpty(mapConfig.getDailyLimitList())) {
            // 从限制列表中查找对应apiCode的限制信息
            for (ApiLimitInfo limitInfo : mapConfig.getDailyLimitList()) {
                if (limitInfo.getApiCode().equals(apiCode)) {
                    limitValue = limitInfo.getDailyLimit();
                    break;
                }
            }
        }

        // 如果找到限制且不为负数，则进行限流检查
        if (limitValue != null && limitValue >= 0) {
            boolean exceed;
            Date nowDate = DateUtils.getNowDate();
            if (mapConfig.isCorpConfig()) {
                exceed = mapConfigService.isExceedLimit(corpId, mapType, mapConfig.getApiKey(),
                        limitValue, nowDate, apiCode);
            } else {
                exceed = mapConfigService.isExceedLimit(mapType, mapConfig.getApiKey(),
                        limitValue, nowDate, apiCode);
            }

            if (exceed) {
                log.info("[地图API] 已超过调用限制, 企业ID: {}, 密钥: {}, 限制: {}, 接口类型: {}",
                        corpId, mapConfig.getApiKey(), limitValue, apiCode);
                throw new CustomException("地图API调用次数已达到上限");
            }

            // 记录API调用次数
            try {
                // 没有超过调用限制, 允许调用, 添加限制记录
                if (mapConfig.isCorpConfig()) {
                    mapConfigService.recordApiCall(corpId, mapType, mapConfig.getApiKey(), nowDate, apiCode);
                } else {
                    mapConfigService.recordApiCall(mapType, mapConfig.getApiKey(), nowDate, apiCode);
                }
            } catch (Exception e) {
                log.error("[地图API] 记录调用失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 检查API调用限制并处理API密钥（兼容老接口）
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     */
    public void checkApiLimit(String corpId, Integer mapType) {
        // 使用默认接口类型（为保证兼容性，这里使用DISTRICT_LIST）
        checkApiLimit(corpId, mapType, MapApiCodeEnum.DISTRICT_LIST.getCode());
    }
} 