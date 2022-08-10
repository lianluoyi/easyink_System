package com.easyink.wecom.domain.dto.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 类名： MomentCommentsDTO
 *
 * @author 佚名
 * @date 2022/1/6 16:01
 */
@Data
@ApiModel("朋友圈的互动数据DTO")
public class MomentCommentsDTO {
    @ApiModelProperty(value = "朋友圈id,仅支持企业发表的朋友圈id", required = true)
    @NotBlank(message = "朋友圈id不能为空")
    private String moment_id;

    @ApiModelProperty("企业发表成员userid，如果是企业创建的朋友圈，可以通过获取客户朋友圈企业发表的列表获取已发表成员userid，如果是个人创建的朋友圈，创建人userid就是企业发表成员userid")
    private String userid;

}
