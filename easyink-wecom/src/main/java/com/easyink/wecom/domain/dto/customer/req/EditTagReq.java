package com.easyink.wecom.domain.dto.customer.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Forest请求中的Body实体类，用于编辑客户标签重试请求中
 *
 * @author lichaoyu
 * @date 2023/6/28 16:25
 */
@Data
public class EditTagReq {

    @ApiModelProperty("参数名称")
    String name;
    @ApiModelProperty("参数类型")
    String type;
    @ApiModelProperty("参数的值")
    String[] value;
}
