package com.easyink.wecom.domain.vo.moment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名： 朋友圈成员执行情况VO
 *
 * @author 佚名
 * @date 2022/1/13 16:50
 */
@Data
@ApiModel("朋友圈成员执行情况VO")
public class MomentTotalVO {
    @ApiModelProperty("员工总数")
    private Integer userNum;
    @ApiModelProperty("已发布")
    private Integer publishNum;
    @ApiModelProperty("待发布")
    private Integer notPublishNum;
    @ApiModelProperty("过期朋友圈")
    private Integer expireNum;
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;
}
