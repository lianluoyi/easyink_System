package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.wecom.domain.dto.app.WePermanentCodeResp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

/**
 * 类名: WeAuthCorpInfo
 *
 * @author: 1*+
 * @date: 2021-09-09 20:16
 */
@Data
@TableName("we_auth_corp_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "授权企业信息实体")
public class WeAuthCorpInfo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "授权企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "第三方应用的SuiteId")
    @TableField("suite_id")
    private String suiteId;

    @ApiModelProperty(value = "永久授权码")
    @TableField("permanent_code")
    private String permanentCode;

    @ApiModelProperty(value = "授权方企业名称")
    @TableField("corp_name")
    private String corpName;

    @ApiModelProperty(value = "授权方企业类型，认证号：verified, 注册号：unverified")
    @TableField("corp_type")
    private String corpType;

    @ApiModelProperty(value = "授权方企业方形头像")
    @TableField("corp_square_logo_url")
    private String corpSquareLogoUrl;

    @ApiModelProperty(value = "授权方企业用户规模")
    @TableField("corp_user_max")
    private Integer corpUserMax;

    @ApiModelProperty(value = "授权方企业应用数上限")
    @TableField("corp_agent_max")
    private Integer corpAgentMax;

    @ApiModelProperty(value = "授权方企业的主体名称(仅认证或验证过的企业有)，即企业全称")
    @TableField("corp_full_name")
    private String corpFullName;

    @ApiModelProperty(value = "企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队")
    @TableField("subject_type")
    private Integer subjectType;

    @ApiModelProperty(value = "认证到期时间")
    @TableField("verified_end_time")
    private Date verifiedEndTime;

    @ApiModelProperty(value = "授权企业在微工作台（原企业号）的二维码，可用于关注微工作台")
    @TableField("corp_wxqrcode")
    private String corpWxqrcode;

    @ApiModelProperty(value = "企业规模。当企业未设置该属性时，值为空")
    @TableField("corp_scale")
    private String corpScale;

    @ApiModelProperty(value = "企业所属行业。当企业未设置该属性时，值为空")
    @TableField("corp_industry")
    private String corpIndustry;

    @ApiModelProperty(value = "企业所属子行业。当企业未设置该属性时，值为空")
    @TableField("corp_sub_industry")
    private String corpSubIndustry;

    @ApiModelProperty(value = "取消授权(0N1Y)")
    @TableField("cancel_auth")
    private Boolean cancelAuth;

    @ApiModelProperty(value = "授权企业信息扩展实体")
    @TableField(exist = false)
    private WeAuthCorpInfoExtend corpInfoExtend;


    public WeAuthCorpInfo(WePermanentCodeResp wePermanentCodeResp) {
        if (wePermanentCodeResp == null || ObjectUtils.isEmpty(wePermanentCodeResp.getAuthCorpInfo())) {
            return;
        }
        this.corpId = wePermanentCodeResp.getAuthCorpInfo().getCorpId();
        this.permanentCode = wePermanentCodeResp.getPermanentCode();
        this.corpName = wePermanentCodeResp.getAuthCorpInfo().getCorpName();
        this.corpType = wePermanentCodeResp.getAuthCorpInfo().getCorpType();
        this.corpSquareLogoUrl = wePermanentCodeResp.getAuthCorpInfo().getCorpSquareLogoUrl();
        if (ObjectUtils.isEmpty(wePermanentCodeResp.getAuthCorpInfo().getCorpUserMax())) {
            this.corpUserMax = 0;
        } else {
            this.corpUserMax = Integer.valueOf(wePermanentCodeResp.getAuthCorpInfo().getCorpUserMax());
        }
        if (ObjectUtils.isEmpty(wePermanentCodeResp.getAuthCorpInfo().getCorpAgentMax())) {
            this.corpAgentMax = 0;
        } else {
            this.corpAgentMax = Integer.valueOf(wePermanentCodeResp.getAuthCorpInfo().getCorpAgentMax());
        }
        this.corpFullName = wePermanentCodeResp.getAuthCorpInfo().getCorpFullName();
        this.subjectType = wePermanentCodeResp.getAuthCorpInfo().getSubjectType();
        if (ObjectUtils.isEmpty(wePermanentCodeResp.getAuthCorpInfo().getVerifiedEndTime())) {
            this.verifiedEndTime = new Date();
        } else {
            this.verifiedEndTime = new Date(wePermanentCodeResp.getAuthCorpInfo().getVerifiedEndTime() * 1000L);
        }
        this.corpWxqrcode = wePermanentCodeResp.getAuthCorpInfo().getCorpWxQrcode();
        this.corpScale = wePermanentCodeResp.getAuthCorpInfo().getCorpScale();
        this.corpIndustry = wePermanentCodeResp.getAuthCorpInfo().getCorpIndustry();
        this.corpSubIndustry = wePermanentCodeResp.getAuthCorpInfo().getCorpSubIndustry();
    }


}
