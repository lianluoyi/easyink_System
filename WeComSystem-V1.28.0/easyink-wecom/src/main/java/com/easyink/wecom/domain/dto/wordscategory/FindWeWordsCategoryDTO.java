package com.easyink.wecom.domain.dto.wordscategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名：FindWeWordsCategoryDTO
 *
 * @author Society my sister Li
 * @date 2021-10-27 16:57
 */
@Data
@ApiModel("查询话术文件夹列表")
public class FindWeWordsCategoryDTO {

    @ApiModelProperty(value = "父文件夹ID")
    private Long parentId;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(value = "文件夹类型", required = true)
    @NotNull(message = "type不能为空")
    private Integer type;

    @ApiModelProperty(value = "查询范围", hidden = true)
    private String useRange;

    @ApiModelProperty(value = "文件夹名称")
    private String name;
}
