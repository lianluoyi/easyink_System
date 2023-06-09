package com.easyink.wecom.domain.vo.autotag;

import com.easyink.wecom.domain.autotag.WeBatchTagTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询批量标签任务VO
 *
 * @author lichaoyu
 * @date 2023/6/5 16:30
 */
@Data
@ApiModel("查询批量标签任务VO")
public class BatchTagTaskVO extends WeBatchTagTask{

    @ApiModelProperty(value = "客户总数")
    private Integer customerCnt = 0;

    @ApiModelProperty(value = "部门名称")
    private String department;
}
