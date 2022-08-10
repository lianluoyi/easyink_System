package com.easyink.wecom.domain.dto.wordscategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：WeWordsCategoryChildSortDTO
 *
 * @author Society my sister Li
 * @date 2021-10-27 17:23
 */
@Data
@ApiModel("子文件夹排序")
public class WeWordsCategoryChildSortDTO {

    @ApiModelProperty("子文件夹ID")
    private Long childId;

    @ApiModelProperty("子文件夹名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;
}
