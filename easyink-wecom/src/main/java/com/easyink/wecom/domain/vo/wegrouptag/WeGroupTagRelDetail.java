package com.easyink.wecom.domain.vo.wegrouptag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：WeGroupTagRelDetail
 *
 * @author Society my sister Li
 * @date 2021-11-15 16:26
 */
@Data
@ApiModel("客户群的标签详情")
public class WeGroupTagRelDetail {

    @ApiModelProperty("标签ID")
    private Long tagId;

    @ApiModelProperty("标签名")
    private String name;
}