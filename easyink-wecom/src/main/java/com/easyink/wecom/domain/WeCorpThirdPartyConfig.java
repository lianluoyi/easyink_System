package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 企业第三方服务推送配置对象 we_corp_third_party_config
 *
 * @author easyink
 * @date 2024-01-01
 */
@ApiModel("企业第三方服务推送配置")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_corp_third_party_config")
public class WeCorpThirdPartyConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 企业ID */
    @ApiModelProperty(value = "企业ID", required = true)
    @TableId( type = IdType.INPUT)
    @TableField("corp_id")
    @NotBlank(message = "企业ID不能为空")
    private String corpId;

    /** 第三方推送URL */
    @ApiModelProperty(value = "第三方推送URL", required = true)
    @TableField("push_url")
    @NotBlank(message = "推送URL不能为空")
    private String pushUrl;

    /** 状态(0:停用 1:启用) */
    @ApiModelProperty(value = "状态(0:停用 1:启用)")
    @TableField("status")
    private Integer status;


}
