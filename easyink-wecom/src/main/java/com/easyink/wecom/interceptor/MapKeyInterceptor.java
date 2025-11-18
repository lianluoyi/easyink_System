package com.easyink.wecom.interceptor;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.StringUtils;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.enums.MapTypeEnum;
import com.easyink.wecom.domain.model.mapapi.WeMapConfigModel;
import com.easyink.wecom.service.WeMapConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import static com.easyink.common.constant.WeConstans.KEY_ERROR;

/**
 * 地图API密钥拦截器
 * 只负责将请求中的key参数替换为实际的API密钥
 * API调用限制逻辑已移至MapServiceImpl
 *
 * @author wx
 * @date 2023/8/8
 */
@Slf4j
public class MapKeyInterceptor implements Interceptor<Object> {

    /**
     * 统一异常错误信息
     */
    private static final String GET_MAP_ERROR_MSG = "地址数据获取失败";
    /**
     * 地图接口状态值
     */
    private static final String STATUS = "status";

    /**
     * 前置处理，替换密钥
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        try {
            String corpId = (String) request.getQuery("corpId");
            // 获取当前企业ID
            if (StringUtils.isBlank(corpId)) {
                log.error("[地图API] 获取企业ID失败，无法替换密钥");
                return false;
            }

            // 根据请求路径判断是哪种地图API
            Integer mapType = determineMapType(request.getUrl());

            // 获取API密钥配置
            WeMapConfigModel config = SpringUtils.getBean(WeMapConfigService.class).getMapConfig(corpId, mapType);

            if (config == null || StringUtils.isBlank(config.getApiKey())) {
                log.error("[地图API] 未找到有效的API密钥配置, 企业ID: {}, 地图类型: {}", corpId, mapType);
                return false;
            }

            // 替换请求中的key参数
            request.addQuery("key", config.getApiKey());

            return true;
        } catch (Exception e) {
            log.error("[地图API] 密钥替换异常: {}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }


    /**
     * 根据请求URL判断地图类型
     *
     * @param url 请求URL
     * @return 地图类型
     */
    private Integer determineMapType(String url) {
        // 默认返回腾讯地图
        return MapTypeEnum.TENCENT.getCode();
    }

    /**
     * 请求成功后的处理
     */
    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("[地图API] url:【{}】,result:【{}】", request.getUrl(), response.getContent());
        JSONObject result = JSONUtil.toBean(response.getContent(), JSONObject.class);
        if (null != result && !WeConstans.WE_SUCCESS_CODE.equals(result.getInteger(STATUS)) ) {
            if(KEY_ERROR.equals(result.getInteger(STATUS))){
                throw new CustomException(ResultTip.TIP_MAP_API_KEY_ERROR);
            }
            throw new ForestRuntimeException(GET_MAP_ERROR_MSG);
        }
    }

    /**
     * 请求失败后的处理
     */
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        log.error("[地图API]请求失败url:【{}】,result:【{}】", request.getUrl(), response.getContent());
        throw new ForestRuntimeException(GET_MAP_ERROR_MSG);
    }

} 