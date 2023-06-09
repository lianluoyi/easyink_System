package com.easyink.wecom.domain.dto.autotag.batchtag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 批量标签导入任务DTO
 *
 * @author lichaoyu
 * @date 2023/6/5 14:26
 */
@Data
@ApiModel("批量标签导入任务DTO")
public class ImportBatchTagTaskDTO {
    
    @ApiModelProperty(value = "企业ID")
    private String corpId;

    @ApiModelProperty(value = "批量打标签任务名称")
    private String taskName;

    @ApiModelProperty(value = "标签ID列表")
    private List<String> tagIds;

}
