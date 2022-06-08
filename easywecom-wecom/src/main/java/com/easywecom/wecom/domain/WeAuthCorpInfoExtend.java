package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.wecom.domain.dto.app.WePermanentCodeResp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 类名: WeAuthCorpInfoExtend
 *
 * @author: 1*+
 * @date: 2021-09-09 20:16
 */
@Data
@TableName("we_auth_corp_info_extend")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "授权企业信息扩展实体")
public class WeAuthCorpInfoExtend {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "授权企业ID")
    @TableId
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "第三方应用的SuiteId")
    @TableField("suite_id")
    private String suiteId;

    @ApiModelProperty(value = "授权方应用id")
    @TableField("agentid")
    private String agentid;

    @ApiModelProperty(value = "授权方应用名字")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "授权方应用方形头像")
    @TableField("square_logo_url")
    private String squareLogoUrl;

    @ApiModelProperty(value = "授权方应用圆形头像")
    @TableField("round_logo_url")
    private String roundLogoUrl;

    @ApiModelProperty(value = "授权模式，0为管理员授权；1为成员授权")
    @TableField("auth_mode")
    private Integer authMode;

    @ApiModelProperty(value = "是否为代开发自建应用(0N1Y)")
    @TableField("is_customized_app")
    private Boolean isCustomizedApp;

    @ApiModelProperty(value = "授权管理员的userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）")
    @TableField("auth_user_info_userid")
    private String authUserInfoUserid;

    @ApiModelProperty(value = "授权管理员的open_userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）")
    @TableField("auth_user_info_open_userid")
    private String authUserInfoOpenUserid;

    @ApiModelProperty(value = "授权管理员的name，可能为空")
    @TableField("auth_user_info_name")
    private String authUserInfoName;

    @ApiModelProperty(value = "授权管理员的头像url，可能为空")
    @TableField("auth_user_info_avatar")
    private String authUserInfoAvatar;

    @ApiModelProperty(value = "代理服务商企业微信id")
    @TableField("dealer_corp_info_corpid")
    private String dealerCorpInfoCorpid;

    @ApiModelProperty(value = "代理服务商企业微信名称")
    @TableField("dealer_corp_info_corp_name")
    private String dealerCorpInfoCorpName;

    @ApiModelProperty(value = "注册码 , 最长为512个字节")
    @TableField("register_code_info_register_code")
    private String registerCodeInfoRegisterCode;

    @ApiModelProperty(value = "推广包ID，最长为128个字节")
    @TableField("register_code_info_template_id")
    private String registerCodeInfoTemplateId;

    @ApiModelProperty(value = "用户自定义的状态值。只支持英文字母和数字，最长为128字节")
    @TableField("register_code_info_state")
    private String registerCodeInfoState;


    public WeAuthCorpInfoExtend(WePermanentCodeResp wePermanentCodeResp) {
        if (wePermanentCodeResp == null || wePermanentCodeResp.getAuthCorpInfo() == null) {
            return;
        }
        if (StringUtils.isBlank(wePermanentCodeResp.getAuthCorpInfo().getCorpId())) {
            this.corpId = "";
        } else {
            this.corpId = wePermanentCodeResp.getAuthCorpInfo().getCorpId();
        }

        if (wePermanentCodeResp.getAuthInfo() != null
                && CollectionUtils.isNotEmpty(wePermanentCodeResp.getAuthInfo().getAgent())
                && wePermanentCodeResp.getAuthInfo().getAgent().get(0) != null) {
            this.agentid = String.valueOf(wePermanentCodeResp.getAuthInfo().getAgent().get(0).getAgentId());
            this.name = wePermanentCodeResp.getAuthInfo().getAgent().get(0).getName();
            this.squareLogoUrl = wePermanentCodeResp.getAuthInfo().getAgent().get(0).getSquareLogoUrl();
            this.roundLogoUrl = wePermanentCodeResp.getAuthInfo().getAgent().get(0).getRoundLogoUrl();
            this.authMode = wePermanentCodeResp.getAuthInfo().getAgent().get(0).getAuthMode();
            this.isCustomizedApp = wePermanentCodeResp.getAuthInfo().getAgent().get(0).getIsCustomizedApp();
        }

        if (wePermanentCodeResp.getAuthUserInfo() != null) {
            this.authUserInfoUserid = wePermanentCodeResp.getAuthUserInfo().getUserId();
            this.authUserInfoOpenUserid = wePermanentCodeResp.getAuthUserInfo().getOpenUserid();
            this.authUserInfoName = wePermanentCodeResp.getAuthUserInfo().getName();
            this.authUserInfoAvatar = wePermanentCodeResp.getAuthUserInfo().getAvatar();
        }

        if (wePermanentCodeResp.getDealerCorpInfo() != null) {
            this.dealerCorpInfoCorpid = wePermanentCodeResp.getDealerCorpInfo().getCorpid();
            this.dealerCorpInfoCorpName = wePermanentCodeResp.getDealerCorpInfo().getCorpName();
        }

        if (wePermanentCodeResp.getRegisterCodeInfo() != null) {
            this.registerCodeInfoRegisterCode = wePermanentCodeResp.getRegisterCodeInfo().getRegisterCode();
            this.registerCodeInfoTemplateId = wePermanentCodeResp.getRegisterCodeInfo().getTemplateId();
            this.registerCodeInfoState = wePermanentCodeResp.getRegisterCodeInfo().getState();
        }
    }
}
