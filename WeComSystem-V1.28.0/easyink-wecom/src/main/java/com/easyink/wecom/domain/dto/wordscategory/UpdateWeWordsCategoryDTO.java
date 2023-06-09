package com.easyink.wecom.domain.dto.wordscategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名：UpdateWeWordsCategoryDTO
 *
 * @author Society my sister Li
 * @date 2021-10-27 16:57
 */
@Data
@ApiModel("编辑话术文件夹")
public class UpdateWeWordsCategoryDTO {

    @ApiModelProperty(value = "文件夹ID", required = true)
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "文件夹类型", required = true)
    @NotNull(message = "type不能为空")
    private Integer type;

    @ApiModelProperty(value = "文件夹名称", required = true)
    @NotBlank(message = "name不能为空")
    private String name;

    @ApiModelProperty(value = "上级文件夹ID")
    private Long parentId;

    @ApiModelProperty(value = "下级文件夹ID列表")
    private List<WeWordsCategoryChildSortDTO> childIdList;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty("要删除的子文件件ID")
    private List<Long> delChildList;
}
