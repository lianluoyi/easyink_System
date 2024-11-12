package com.easyink.wecom.service.wechatopen;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dtflys.forest.http.ForestRequest;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.AppIdVO;
import com.easyink.wecom.domain.vo.WeOpenConfigVO;

import java.util.List;

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
     * @param shortCode 短链
     * @return appId 于配置文件配置
     */
    AppIdVO getAppIdByShortCode(String shortCode);

    /**
     * 通过表单Id获取系统的appId配置
     *
     * @param formId    表单id
     * @return appId 于配置文件配置
     */
    AppIdVO getAppIdByFormId(Long formId);

    /**
     * 获取用户的openId
     *
     * @param code   前端传来的code
     * @param corpId 企业id
     * @param appId  公众号appid
     * @return 获取到openId
     */
    String getOpenId(String code, String corpId, String appId);

    /**
     * 为企微开放平台请求设置accessToken
     *
     * @param request forest请求
     */
    void setAccessToken(ForestRequest request);

    /**
     * 为微信开放平台-三丰平台设置token
     *
     * @param appId
     * @return
     */
    String getPlatform3rdAccessToken(String appId);

    /**
     * 获取公众号域名
     *
     * @param corpId    企业id
     * @return
     */
    String getDomain(String corpId);

    /**
     * 根据openid获取unionid
     *
     * @param openId 公众号openid
     * @return unionid
     */
    String getUnionIdByOpenId(String openId);

    /**
     * 当前企业的获取公众平台配置
     *
     * @param corpId 企业id
     * @return {@link WeOpenConfigVO}
     */
    List<WeOpenConfigVO> getConfigs(String corpId);

    /**
     * 获取公众平台配置
     *
     * @param corpId 企业id
     * @param appId  公众号appid
     * @return {@link WeOpenConfig}
     */
    WeOpenConfig getConfig(String corpId, String appId);

    /**
     * 修改企微的公众平台配置
     *
     * @param config   {@link WeOpenConfig }
     * @param isOnline 是否在线
     */
    void updateConfig(WeOpenConfig config, boolean isOnline);

    /**
     * 授权成功，新增一公众号配置（待开发应用）
     *
     * @param corpId            企业id
     * @param userId            创建人id
     * @param authorizationCode 授权码
     */
    void addOrUpdate3rdConfig(String corpId, String userId, String authorizationCode);

    /**
     * 授权成功、更新 (第三方平台回调通知使用)
     *
     * @param authorizationCode 授权码
     */
    void addOrUpdate3rdConfig(String authorizationCode);

    /**
     * 取消授权，移除公众号配置（待开发应用）
     *
     * @param authorizerAppid   取消授权方（公众号）appid
     */
    void remove3rdConfig(String authorizerAppid);

    /**
     * 获取微信三方平台授权页url （待开发应用）
     * 授权成功通知、授权更新通知、授权成功回调 会调用该方法
     *
     * @param corpId 企业id
     *
     * @return url
     */
    String getWechatOpen3rdAuthUrl(String corpId);

    /**
     * 处理三方平台授权公众号回调
     *
     * @param corpId    企业id
     * @param userId    员工id
     * @param authCode  授权码
     * @return
     */
    String handle3rdAuthOfficeAccount(String corpId, String userId, String authCode);


}
