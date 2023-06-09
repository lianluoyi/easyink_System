package com.easyink.wecom.domain.vo.autotag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导入批量打标签任务结果VO
 *
 * @author lichaoyu
 * @date 2023/6/5 11:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "批量打标签任务导入结果")
public class ImportBatchTagTaskVO {

    @ApiModelProperty("成功数量")
    private Integer successNum;

    @ApiModelProperty("失败数量")
    private Integer failNum;

    @ApiModelProperty("失败报告")
    private String url;

}
