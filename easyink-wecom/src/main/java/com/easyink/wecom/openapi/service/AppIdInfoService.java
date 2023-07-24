package com.easyink.wecom.openapi.service;


import com.easyink.wecom.openapi.domain.entity.AppIdInfo;
import com.easyink.wecom.openapi.domain.vo.AppIdGenVO;

import javax.validation.constraints.NotNull;

/**
 * 类名: 开发参数生成接口
 *
 * @author : silver_chariot
 * @date : 2022/3/14 13:37
 */
public interface AppIdInfoService {


    /**
     * 获取账号的open api 配置
     *
     * @param corpId 企业ID
     * @return app_id & app_secret {@link AppIdInfo}
     */
    AppIdInfo get(String corpId);

    /**
     * 获取账号的open api 配置
     *
     * @param corpId 企业ID
     * @return app_id  {@link AppIdGenVO} 只返回app_id
     */
    AppIdGenVO getVO(String corpId);

    /**
     * 初始化开发数据
     *
     * @param corpId 企业ID
     * @return app_id & app_secret {@link AppIdGenVO}
     */
    AppIdGenVO create(String corpId);

    /**
     * 刷新秘钥
     *
     * @param corpId 企业ID
     * @return app_id & app_secret {@link AppIdGenVO}
     */
    AppIdGenVO refreshSecret(String corpId);

    /**
     * 获取票据
     *
     * @param appId     appId
     * @param appSecret appSecret
     * @return 票据
     */
    String getTicket(@NotNull(message = "appId cannot be null") String appId, String appSecret);

}
