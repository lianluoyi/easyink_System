package com.easywecom.wecom.domain.dto.wordscategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：WeWordsCategoryChangeSort
 *
 * @author Society my sister Li
 * @date 2021-11-04 09:24
 */
@Data
@ApiModel("文件夹修改sort")
public class WeWordsCategoryChangeSort {

    @ApiModelProperty(value = "文件夹ID", required = true)
    private Long id;

    @ApiModelProperty(value = "新sort", required = true)
    private Integer sort;
}
