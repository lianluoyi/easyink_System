package com.easyink.common.core.domain.wecom;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: 扩展字段关系基础实体
 *
 * @author : silver_chariot
 * @date : 2021/11/15 19:40
 */
@Data
public class BaseExtendPropertyRel {

    @TableField("corp_id")
    @ApiModelProperty(value = "公司id")
    private String corpId;

    @TableField("external_userid")
    @ApiModelProperty(value = "客户id")
    private String externalUserid;

    //@JsonIgnore
    @TableField("user_id")
    @ApiModelProperty(value = "员工id")
    private String userId;

    /**
     * 扩展属性id
     */
    @TableField("extend_property_id")
    @ApiModelProperty(value = "扩展属性id")
    private Long extendPropertyId;
    /**
     * 自定义属性的值
     */
    @TableField("property_value")
    @ApiModelProperty(value = "自定义属性的值")
    private String propertyValue;

    @TableField(exist = false)
    @ApiModelProperty(value = "自定义属性的类型")
    private Integer propertyType;
}
