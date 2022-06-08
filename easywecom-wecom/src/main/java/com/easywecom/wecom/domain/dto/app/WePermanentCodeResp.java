package com.easywecom.wecom.domain.dto.app;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 类名: WePermanentCodeResp
 *
 * @author: 1*+
 * @date: 2021-09-09 20:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WePermanentCodeResp extends WeResultDTO implements Serializable {

    private static final long serialVersionUID = -5028321625140879571L;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private Long expiresIn;

    @SerializedName("permanent_code")
    private String permanentCode;

    /**
     * 授权企业信息
     */
    @SerializedName("auth_corp_info")
    private AuthCorpInfo authCorpInfo;

    /**
     * 授权信息。如果是通讯录应用，且没开启实体应用，是没有该项的。通讯录应用拥有企业通讯录的全部信息读写权限
     */
    @SerializedName("auth_info")
    private AuthInfo authInfo;

    /**
     * 授权用户信息
     */
    @SerializedName("auth_user_info")
    private AuthUserInfo authUserInfo;
    /**
     * 代理服务商企业信息。应用被代理后才有该信息
     */
    @SerializedName("dealer_corp_info")
    private DealerCorpInfo dealerCorpInfo;
    /**
     * 推广二维码安装相关信息，扫推广二维码安装时返回
     */
    @SerializedName("register_code_info")
    private RegisterCodeInfo registerCodeInfo;


    @Data
    public static class AuthCorpInfo implements Serializable {
        private static final long serialVersionUID = -5028321625140879571L;

        @SerializedName("corpid")
        private String corpId;

        @SerializedName("corp_name")
        private String corpName;

        @SerializedName("corp_type")
        private String corpType;

        @SerializedName("corp_square_logo_url")
        private String corpSquareLogoUrl;

        @SerializedName("corp_round_logo_url")
        private String corpRoundLogoUrl;

        @SerializedName("corp_user_max")
        private String corpUserMax;

        @SerializedName("corp_agent_max")
        private String corpAgentMax;

        /**
         * 所绑定的企业微信主体名称(仅认证过的企业有)
         */
        @SerializedName("corp_full_name")
        private String corpFullName;

        /**
         * 认证到期时间
         */
        @SerializedName("verified_end_time")
        private Long verifiedEndTime;

        /**
         * 企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号
         */
        @SerializedName("subject_type")
        private Integer subjectType;

        /**
         * 授权企业在微工作台（原企业号）的二维码，可用于关注微工作台
         */
        @SerializedName("corp_wxqrcode")
        private String corpWxQrcode;

        @SerializedName("corp_scale")
        private String corpScale;

        @SerializedName("corp_industry")
        private String corpIndustry;

        @SerializedName("corp_sub_industry")
        private String corpSubIndustry;

        @SerializedName("location")
        private String location;

    }

    /**
     * 授权信息
     */
    @Data
    public static class AuthInfo implements Serializable {
        private static final long serialVersionUID = -5028321625140879571L;

        /**
         * 授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
         */
        @SerializedName("agent")
        private List<Agent> agent;

    }

    @Data
    public static class Agent implements Serializable {
        private static final long serialVersionUID = -5028321625140879571L;

        @SerializedName("agentid")
        private Integer agentId;

        @SerializedName("name")
        private String name;

        @SerializedName("round_logo_url")
        private String roundLogoUrl;

        @SerializedName("square_logo_url")
        private String squareLogoUrl;

        @SerializedName("auth_mode")
        private Integer authMode;

        @SerializedName("is_customized_app")
        private Boolean isCustomizedApp;

        /**
         * 应用权限
         */
        @SerializedName("privilege")
        private Privilege privilege;

    }

    /**
     * 授权人员信息
     */
    @Data
    public static class AuthUserInfo implements Serializable {
        private static final long serialVersionUID = -5028321625140879571L;

        @SerializedName("userid")
        private String userId;

        @SerializedName("open_userid")
        private String openUserid;

        @SerializedName("name")
        private String name;

        @SerializedName("avatar")
        private String avatar;
    }

    /**
     * 应用对应的权限
     */
    @Data
    public static class Privilege implements Serializable {
        private static final long serialVersionUID = -5028321625140879571L;

        /**
         * 权限等级。
         * 1:通讯录基本信息只读
         * 2:通讯录全部信息只读
         * 3:通讯录全部信息读写
         * 4:单个基本信息只读
         * 5:通讯录全部信息只写
         */
        @SerializedName("level")
        private Integer level;

        @SerializedName("allow_party")
        private List<Integer> allowPartie;

        @SerializedName("allow_user")
        private List<String> allowUser;

        @SerializedName("allow_tag")
        private List<Integer> allowTag;

        @SerializedName("extra_party")
        private List<Integer> extraParty;

        @SerializedName("extra_user")
        private List<String> extraUser;

        @SerializedName("extra_tag")
        private List<Integer> extraTag;


    }


    /**
     * 代理服务商企业信息。应用被代理后才有该信息
     */
    @Data
    public static class DealerCorpInfo implements Serializable {
        private static final long serialVersionUID = -5028321625140879571L;

        @SerializedName("corpid")
        private String corpid;

        @SerializedName("corp_name")
        private String corpName;

    }

    /**
     * 推广二维码安装相关信息，扫推广二维码安装时返回。（注：无论企业是否新注册，只要通过扫推广二维码安装，都会返回该字段）
     */
    @Data
    public static class RegisterCodeInfo implements Serializable {
        private static final long serialVersionUID = -5028321625140879571L;

        @SerializedName("register_code")
        private String registerCode;

        @SerializedName("template_id")
        private String templateId;

        @SerializedName("state")
        private String state;

    }

}
