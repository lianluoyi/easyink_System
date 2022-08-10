package com.easyink.wecom.service.wechatopen;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dtflys.forest.http.ForestRequest;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.AppIdVO;

/**
 * 类名: 微信公众平台业务处理接口
 *
 * @author : silver_chariot
 * @date : 2022/7/20 10:48
 **/
public interface WechatOpenService extends IService<WeOpenConfig> {
    /**
     * 获取系统的appId配置
     *
     * @param shortCode
     *
     * @return appId 于配置文件配置
     */
    AppIdVO getAppId(String shortCode);

    /**
     * 获取用户的openId
     *
     * @param code   前端传来的code
     * @param corpId 企业id
     * @return 获取到openId
     */
    String getOpenId(String code, String corpId);

    /**
     * 为企微开放平台请求设置accessToken
     *
     * @param request forest请求
     */
    void setAccessToken(ForestRequest request);

    /**
     * 根据openid获取unionid
     *
     * @param openId 公众号openid
     * @return unionid
     */
    String getUnionIdByOpenId(String openId);

    /**
     * 获取公众平台配置
     *
     * @param corpId 企业id
     * @return {@link WeOpenConfig}
     */
    WeOpenConfig getConfig(String corpId);

    /**
     * 修改企微的公众平台配置
     *
     * @param config {@link WeOpenConfig }
     */
    void updateConfig(WeOpenConfig config);
}
