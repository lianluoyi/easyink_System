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
 * @date 2022/1/13 14:24
 */
@Data
@ApiModel("朋友圈成员执行情况VO")
public class MomentUserCustomerVO {

    @ApiModelProperty("朋友圈详情结果id")
    private Long id;
    @ApiModelProperty("员工头像")
    private String headImageUrl;
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("员工id")
    private String userId;
    @ApiModelProperty("成员发表状态。0:待发布 1：已发布 2：已过期 3：不可发布")
    private Integer publishStatus;
    @ApiModelProperty("发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date publishTime;
    @ApiModelProperty("客户姓名")
    private String customerName;
    @ApiModelProperty("失败备注")
    private String remark;
    @ApiModelProperty("可见范围（0：全部客户 1：部分客户）")
    private Integer pushRange;
}
