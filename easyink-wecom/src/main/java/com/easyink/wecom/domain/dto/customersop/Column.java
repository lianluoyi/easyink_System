package com.easyink.wecom.domain.dto.customersop;

import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 客户其它属性
 *
 * @author 佚名
 * @date 2021/12/1 20:51
 */
@Data
@ApiModel("客户其它属性DTO <Column>")
public class Column extends BaseExtendPropertyRel {

    /**
     * 额外字段类型 用于从cloumn_info中取出type来判断 来源："addWay"; 出生日期："1"; 日期范围："7";
     */
    @ApiModelProperty(value = "额外字段类型")
    private String type;

}
