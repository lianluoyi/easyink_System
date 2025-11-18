package com.easyink.wecom.service;

import com.easyink.wecom.domain.dto.map.WeMapConfigDTO;
import com.easyink.wecom.domain.entity.WeMapConfig;
import com.easyink.wecom.domain.model.mapapi.WeMapConfigModel;
import com.easyink.wecom.domain.vo.WeMapConfigVO;

import java.util.Date;

/**
 * 地图API配置服务接口
 *
 * @author wx
 * @date 2023/8/3
 */
public interface WeMapConfigService {

    /**
     * 获取系统默认的地图API配置
     *
     * @param mapType 地图类型
     * @return 地图API配置
     */
    WeMapConfig getDefaultConfig(Integer mapType);

    /**
     * 获取企业地图API配置
     *
     * @param corpId 企业ID
     * @param mapType 地图类型
     * @return 配置信息
     */
    WeMapConfig getCorpConfig(String corpId, Integer mapType);

    /**
     * 获取企业地图API配置视图
     *
     * @param corpId 企业ID
     * @param mapType 地图类型
     * @return 配置视图
     */
    WeMapConfigVO getConfigVO(String corpId, Integer mapType);

    /**
     * 保存或更新系统默认地图API配置
     *
     * @param config 配置信息
     * @return 是否成功
     */
    boolean saveOrUpdateDefaultConfig(WeMapConfigDTO config);
    
    /**
     * 保存或更新企业地图API配置
     *
     * @param config 配置信息
     * @return 是否成功
     */
    boolean saveOrUpdateCorpConfig(WeMapConfigDTO config);

    /**
     * 删除企业地图API配置
     *
     * @param corpId 企业ID
     * @param mapType 地图类型
     * @return 是否成功
     */
    boolean deleteCorpConfig(String corpId, Integer mapType);
    
    /**
     * 获取地图API密钥
     *
     * @param corpId 企业ID
     * @param mapType 地图类型
     * @return API密钥信息
     */
    WeMapConfigModel getMapConfig(String corpId, Integer mapType);
    
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
    boolean isExceedLimit(String corpId, Integer mapType, String apiKey, Integer dailyLimit, Date nowDate);

    boolean isExceedLimit(Integer mapType, String apiKey, Integer dailyLimit, Date nowDate);

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
    boolean isExceedLimit(String corpId, Integer mapType, String apiKey, Integer dailyLimit, Date nowDate, Integer apiCode);

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
    boolean isExceedLimit(Integer mapType, String apiKey, Integer dailyLimit, Date nowDate, Integer apiCode);

    /**
     * 记录API调用并增加计数
     *
     * @param corpId  企业ID
     * @param mapType 地图类型
     * @param apiKey  API密钥
     * @param nowDate
     */
    void recordApiCall(String corpId, Integer mapType, String apiKey, Date nowDate);

    void recordApiCall(Integer mapType, String apiKey, Date nowDate);

    /**
     * 记录API调用并增加计数（带接口类型）
     *
     * @param corpId    企业ID
     * @param mapType   地图类型
     * @param apiKey    API密钥
     * @param nowDate   当前日期
     * @param apiCode   接口类型代码
     */
    void recordApiCall(String corpId, Integer mapType, String apiKey, Date nowDate, Integer apiCode);

    /**
     * 记录API调用并增加计数（默认配置，带接口类型）
     *
     * @param mapType   地图类型
     * @param apiKey    API密钥
     * @param nowDate   当前日期
     * @param apiCode   接口类型代码
     */
    void recordApiCall(Integer mapType, String apiKey, Date nowDate, Integer apiCode);

    /**
     * 清除地图API密钥缓存
     *
     * @param corpId 企业ID
     */
    void clearMapKeyCache(String corpId);

}