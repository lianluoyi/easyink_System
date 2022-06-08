package com.easywecom.common.core.domain.wecom;

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
}
