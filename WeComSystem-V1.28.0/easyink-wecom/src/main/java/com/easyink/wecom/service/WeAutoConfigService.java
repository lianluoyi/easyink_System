package com.easyink.wecom.service;

import com.easyink.common.config.ThirdDefaultDomainConfig;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.dto.AutoConfigDTO;
import com.easyink.wecom.domain.vo.WeAdminQrcodeVO;
import com.easyink.wecom.domain.vo.WeCheckQrcodeVO;

/**
 * 类名: 企业微信后台接口处理
 *
 * @author: 1*+
 * @date: 2021-08-23 10:45
 */
public interface WeAutoConfigService {


    /**
     * 获取企业微信后台登录二维码
     *
     * @return
     */
    WeAdminQrcodeVO getAdminQrcode();

    /**
     * 检测二维码
     *
     * @param qrcodeKey 二维码key
     * @param status    二维码状态
     * @param loginUser 当前登录人的企业ID
     * @return {@link WeCheckQrcodeVO}
     */
    WeCheckQrcodeVO check(String qrcodeKey, String status, LoginUser loginUser);

    /**
     * 启动自动配置
     *
     * @param autoConfigDTO autoConfigDTO
     * @param corpId        企业ID
     */
    void autoConfig(AutoConfigDTO autoConfigDTO, LoginUser loginUser);

    /**
     * 获取三方应用服务器默认配置域名
     *
     * @return {@link ThirdDefaultDomainConfig}
     */
    ThirdDefaultDomainConfig getThirdDefaultDomainConfig();

    /**
     * 短信验证
     *
     * @param captcha 验证码
     * @param tlKey   如果需要短信验证,tlKeY会由check接口返回
     * @param qrKey   qrkey由获取二维码接口返回
     */
    void confirmMobileCaptcha(String captcha, String tlKey, String qrKey);

    /**
     * 重新发送短信验证码
     *
     * @param tlKey     tlKey 由check接口返回
     * @param qrcodeKey
     */
    void sendCaptcha(String tlKey, String qrcodeKey);

    /**
     * 获取部门成员隐私信息 （需要管理员扫码授权获取qrcodeKey后)
     *
     * @param corpId    企业corpId
     * @param qrcodeKey 扫码后的qrcodeKey
     */
    void getDepartMemberInfo(String corpId, String qrcodeKey);
}
