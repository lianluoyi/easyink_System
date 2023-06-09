package com.easyink.wecom.domain.dto.wegrouptag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：FindWeGroupTagCategoryDTO
 *
 * @author Society my sister Li
 * @date 2021-11-12 15:51
 */
@Data
@ApiModel("查询群标签组")
public class PageWeGroupTagCategoryDTO {

    @ApiModelProperty("标签组ID")
    private Long id;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty("模糊搜索标签组或标签名称")
    private String searchName;
}
