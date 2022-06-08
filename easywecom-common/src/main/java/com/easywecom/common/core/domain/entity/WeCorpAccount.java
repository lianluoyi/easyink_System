package com.easywecom.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.constant.Constants;
import com.easywecom.common.core.domain.BaseEntity;
import com.easywecom.common.utils.spring.SpringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业id相关配置对象 wx_corp_account
 *
 * @author admin
 * @date 2020-08-24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("企业id配置相关实体")
@TableName("we_corp_account")
public class WeCorpAccount extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "是否为代开发自建应用(0N1Y)")
    @TableField(exist = false)
    private Boolean isCustomizedApp;

    @ApiModelProperty("企业名称")
    @TableField("company_name")
    private String companyName;

    @ApiModelProperty("企业corpId")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("三方企业ID")
    @TableField("external_corp_id")
    private String externalCorpId;

    @ApiModelProperty("企业为corpSecret")
    @TableField("corp_secret")
    private String corpSecret;

    @ApiModelProperty("帐号状态（0正常 1停用 2已授权未启用)")
    @TableField("status")
    private String status;

    @ApiModelProperty("删除标志（0代表存在 2代表删除)")
    @TableField("del_flag")
    private String delFlag = "0";


    @ApiModelProperty("通讯录密钥")
    @TableField("contact_secret")
    private String contactSecret;

    @ApiModelProperty("应用id")
    @TableField("agent_id")
    private String agentId;

    @ApiModelProperty("应用密钥")
    @TableField("agent_secret")
    private String agentSecret;

    @ApiModelProperty("服务商密钥")
    @TableField("provider_secret")
    private String providerSecret;

    @ApiModelProperty("会话存档密钥")
    @TableField("chat_secret")
    private String chatSecret;


    @ApiModelProperty("企业微信扫码登陆回调地址")
    @TableField("wx_qr_login_redirect_uri")
    private String wxQrLoginRedirectUri;

    @ApiModelProperty("客户流失通知开关 0:关闭 1:开启")
    @TableField("customer_churn_notice_switch")
    private String customerChurnNoticeSwitch = Constants.NORMAL_CODE;


    @ApiModelProperty("企业管理员账号")
    @TableField("corp_account")
    private String corpAccount;

    @ApiModelProperty("客户联系密钥")
    @TableField("custom_secret")
    private String customSecret;

    @ApiModelProperty("应用回调aesKey")
    @TableField("encoding_aes_key")
    private String encodingAesKey;

    @ApiModelProperty("H5域名链接")
    @TableField("h5_do_main_name")
    private String h5DoMainName;

    @ApiModelProperty("应用回调token")
    @TableField("token")
    private String token;

    @ApiModelProperty("侧边栏证书")
    @TableField("cert_file_path")
    private String certFilePath;

    @ApiModelProperty("应用回调URL")
    @TableField("callback_uri")
    private String callbackUri;

    /**
     * 重写获取侧边栏域名
     *
     * @return {@link String}
     */
    public String getH5DoMainName() {
        RuoYiConfig ruoYiConfig = SpringUtils.getBean(RuoYiConfig.class);
        if (ruoYiConfig != null && ruoYiConfig.isThirdServer()) {
            return ruoYiConfig.getThirdDefaultDomain().getSidebar();
        }
        return this.h5DoMainName;
    }

}
