package com.easyink.wecom.domain.dto;

/**
 * 类名： WeWordsImportDTO
 *
 * @author 佚名
 * @date 2021/11/1 15:25
 */

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("话术导入DTO")
public class WeWordsImportDTO {
    @ApiModelProperty("分组名")
    @Excel(name = "*分组名(12个字以内)")
    private String categoryName;

    @ApiModelProperty("标题")
    @Excel(name = "话术标题(64字以内)")
    private String title;

    @ApiModelProperty("内容")
    @Excel(name = "*话术内容(1500字以内)")
    private String content;
}
