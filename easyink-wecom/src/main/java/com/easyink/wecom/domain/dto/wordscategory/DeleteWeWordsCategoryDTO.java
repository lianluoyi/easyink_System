package com.easyink.wecom.domain.dto.wordscategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名：DeleteWeWordsCategoryDTO
 *
 * @author Society my sister Li
 * @date 2021-10-27 16:57
 */
@Data
@ApiModel("删除话术文件夹")
public class DeleteWeWordsCategoryDTO {

    @ApiModelProperty(value = "文件夹ID", required = true)
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(hidden = true)
    private String corpId;
}
