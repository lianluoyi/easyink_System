package com.easyink.wecom.domain.vo.wegrouptag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：PageWeGroupTagVO
 *
 * @author Society my sister Li
 * @date 2021-11-23 10:46
 */
@Data
@ApiModel("客户群标签详情数据")
public class PageWeGroupTagVO {

    @ApiModelProperty("标签ID")
    private Long tagId;

    @ApiModelProperty("标签名称")
    private String name;
}
