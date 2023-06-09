package com.easyink.wecom.domain.dto.moment;

import com.easyink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

/**
 * 类名： 朋友圈成员执行情况DTO
 *
 * @author 佚名
 * @date 2022/1/6 15:01
 */
@Data
@ApiModel("朋友圈成员执行情况DTO")
@NoArgsConstructor
public class MomentTaskDTO {
    @ApiModelProperty(value = "朋友圈id,仅支持企业发表的朋友圈id", required = true)
    @NotBlank(message = "朋友圈id不能为空")
    private String moment_id;
    @ApiModelProperty(value = "用于分页查询的游标，字符串类型，由上一次调用返回，首次调用可不填")
    private String cursor;
    @ApiModelProperty(value = "返回的最大记录数，整型，最大值1000，默认值500，超过最大值时取默认值")
    @Max(1000)
    private Integer limit;

    public MomentTaskDTO(String momentId,String nextCursor) {
        this.moment_id = momentId;
        if (StringUtils.isNotBlank(nextCursor)){
            this.cursor = nextCursor;
        }
    }
}
