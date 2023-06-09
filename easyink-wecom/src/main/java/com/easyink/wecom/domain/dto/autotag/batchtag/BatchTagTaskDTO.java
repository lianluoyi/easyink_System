package com.easyink.wecom.domain.dto.autotag.batchtag;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * 查询批量标签任务DTO
 *
 * @author lichaoyu
 * @date 2023/6/5 16:37
 */
@ApiModel("查询批量标签任务DTO")
@Data
public class BatchTagTaskDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "企业ID")
    private String corpId;

    @ApiModelProperty(value = "批量打标签任务名称")
    private String taskName;

    @ApiModelProperty(value = "标签ID")
    private List<String> tagIds;

    @ApiModelProperty(value = "批量打标签任务执行状态")
    private Boolean executeFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

}
