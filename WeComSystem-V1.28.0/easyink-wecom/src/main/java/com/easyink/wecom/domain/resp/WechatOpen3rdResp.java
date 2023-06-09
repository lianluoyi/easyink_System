package com.easyink.wecom.domain.resp;

import com.easyink.common.enums.wechatopen.WechatOpenEnum;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 微信开放平台-第三方平台接口响应
 *
 * @author wx
 * 2023/1/10 10:28
 **/
@Data
public class WechatOpen3rdResp {

    /**
     * 获取第三方平台component_access_token 响应值
     */
    @Data
    public static class ComponentAccessToken extends WechatOpenBaseResp{

        /**
         * 第三方平台access_token
         */
        private String component_access_token;

        /**
         * 过期时间
         */
        private Integer expires_in;
    }

    /**
     * 获取预授权码pre_auth_code 响应值
     */
    @Data
    public static class PreAuthCode {

        /**
         * 预授权码
         */
        private String pre_auth_code;

        /**
         * 有效期，为10分钟
         */
        private Integer expires_in;
    }

    /**
     * 授权信息 响应值
     */
    @Data
    public static class AuthorizationInfo extends AuthorizerToken{
        /**
         * 授权方appid
         */
        private String authorizer_appid;

        /**
         * 	授权给开发者的权限集列表(暂时没用到)
         */
        private FuncScopeCategory[] func_info;
    }

    /**
     * 公众号授权给开发者的权限集列表
     */
    @Data
    private static class FuncScopeCategory extends WechatOpenBaseResp{
        /**
         * ID为1到26分别代表： 1、消息管理权限 2、用户管理权限 3、帐号服务权限 4、网页服务权限 5、微信小店权限 6、微信多客服权限 7、群发与通知权限
         *              	8、微信卡券权限 9、微信扫一扫权限 10、微信连WIFI权限 11、素材管理权限 12、微信摇周边权限 13、微信门店权限 15、自定义菜单权限 16、获取认证状态及信息 17、帐号管理权限（小程序）
         *	                18、开发管理与数据分析权限（小程序） 19、客服消息管理权限（小程序） 20、微信登录权限（小程序） 21、数据分析权限（小程序） 22、城市服务接口权限 23、广告管理权限 24、开放平台帐号管理权限
         * 	                25、 开放平台帐号管理权限（小程序） 26、微信电子发票权限 41、搜索widget的权限 请注意： 1）该字段的返回不会考虑公众号是否具备该权限集的权限（因为可能部分具备），
         *  请根据公众号的帐号类型和认证情况，来判断公众号的接口权限。(暂时没用到)
         */
        private Integer id;
    }

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息 响应值
     */
    public AuthorizationInfo authorization_info;


    /**
     * 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）响应值
     */
    @Data
    public static class AuthorizerToken extends WechatOpenBaseResp{
        /**
         * 授权方令牌
         */
        private String authorizer_access_token;

        /**
         * 有效期，为2小时
         */
        private Integer expires_in;

        /**
         * 刷新令牌
         */
        private String authorizer_refresh_token;
    }

    /**
     * 授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
     */
    @Data
    public static class ServiceTypeInfo {
        private Integer id;
    }

    /**
     * 授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，
     * 4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证 (暂时没用到)
     */
    @Data
    public static class VerifyTypeInfo {
        private Integer id;
    }

    /**
     * 授权方的帐号基本信息
     */
    @Data
    public static class GetAuthorizerInfo extends WechatOpenBaseResp{
        /**
         * 授权方昵称
         */
        private String nick_name;

        /**
         * 授权方头像
         */
        private String head_img;

        /**
         * 授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
         */
        private ServiceTypeInfo service_type_info;

        /**
         * 授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，
         * 4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证 (暂时没用到)
         */
        private VerifyTypeInfo verify_type_info;

        /**
         * 授权方公众号的原始ID (暂时没用到)
         */
        private String user_name;

        /**
         * 授权方公众号所设置的微信号，可能为空
         */
        private String alias;

        /**
         * 二维码图片的URL
         */
        private String qrcode_url;

        /**
         * 公众号的主体名称
         */
        private String principal_name;

        /**
         * 获取WeOpenConfig实体
         *
         * @return {@link WeOpenConfig}
         */
        public WeOpenConfig getWechatOpenConfig(){
            WeOpenConfig weOpenConfig = new WeOpenConfig();
            weOpenConfig.setNickName(StringUtils.defaultString(nick_name));
            weOpenConfig.setHeadImg(StringUtils.defaultString(head_img));
            weOpenConfig.setPrincipalName(StringUtils.defaultString(principal_name));
            weOpenConfig.setServiceTypeInfo(WechatOpenEnum.ServiceType.getByCode(service_type_info == null ? null : service_type_info.getId()).getCode());
            return weOpenConfig;
        }
    }

    /**
     * 获取授权方的帐号基本信息 响应值
     */
    public GetAuthorizerInfo authorizer_info;

}
