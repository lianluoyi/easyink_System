package com.easyink.wecom.domain.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 修改话术所属文件夹DTO
 *
 * @author 佚名
 * @date 2021/10/28 16:04
 */
@Data
@ApiModel("修改话术所属文件夹DTO")
public class WeWordsModifyCategoryDTO {
    @ApiModelProperty("要转移的文件夹id")
    private Long categoryId;
    @ApiModelProperty("话术id列表")
    private List<Long> ids;
}
