package com.easyink.wecom.domain.dto.wordscategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：WeWordsCategoryChangeSortDTO 文件夹修改顺序
 *
 * @author Society my sister Li
 * @date 2021-11-04 09:23
 */
@Data
@ApiModel("文件夹修改顺序")
public class WeWordsCategoryChangeSortDTO {

    @ApiModelProperty(value = "文件夹ID及新的sort", required = true)
    private List<WeWordsCategoryChangeSort> sortList;

    @ApiModelProperty(hidden = true)
    private String corpId;
}
