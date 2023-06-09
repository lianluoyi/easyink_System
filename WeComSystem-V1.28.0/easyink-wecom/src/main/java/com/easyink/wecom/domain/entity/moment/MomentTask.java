package com.easyink.wecom.domain.entity.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： MomentTask
 *
 * @author 佚名
 * @date 2022/1/6 15:10
 */
@ApiModel("朋友圈成员发表任务")
@Data
public class MomentTask {
    @ApiModelProperty("发表成员用户userid")
    private String userid;
    @ApiModelProperty("成员发表状态。0:未发表 1：已发表")
    private Integer publish_status;
}
