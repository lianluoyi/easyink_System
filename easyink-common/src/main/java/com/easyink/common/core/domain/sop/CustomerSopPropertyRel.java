package com.easyink.common.core.domain.sop;

import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 客户SOP扩展字段关系
 *
 * @author lichaoyu
 * @date 2023/5/30 10:27
 */
public class CustomerSopPropertyRel extends BaseExtendPropertyRel {

    /**
     * 额外字段类型 用于从cloumn_info中取出type来判断 来源："addWay"; 出生日期："1"; 日期范围："7";
     */
    @ApiModelProperty(value = "额外字段类型")
    private String type;
}
