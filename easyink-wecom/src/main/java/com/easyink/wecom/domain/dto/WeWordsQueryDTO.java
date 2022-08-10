package com.easyink.wecom.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 类名： WeWordsQueryDTO
 *
 * @author 佚名
 * @date 2021/10/28 11:04
 */
@ApiModel("话术库查询DTO")
@Data
public class WeWordsQueryDTO {
    @ApiModelProperty("主键")
    private Integer id;
    @ApiModelProperty(value = "排序值", hidden = true)
    private Long sort;

    @ApiModelProperty("查询内容")
    private String content;

    @ApiModelProperty("文件夹id列表")
    @NotEmpty(message = "文件夹id列表不能为空")
    private List<Long> categoryIds;

    @ApiModelProperty("页面条数")
    private Integer pageSize;

    @ApiModelProperty("页码")
    private Integer pageNum;


    @JsonIgnore
    private String corpId;
}
