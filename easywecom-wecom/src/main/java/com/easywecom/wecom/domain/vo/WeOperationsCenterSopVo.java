package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名： sop查询结果VO
 *
 * @author 佚名
 * @date 2021/11/30 14:35
 */
@Data
@ApiModel("sop查询结果VO")
public class WeOperationsCenterSopVo {

    @ApiModelProperty(value = "创建人.员工userId")
    private String createBy;

    @ApiModelProperty(value = "SOP名称")
    private String name;

    @ApiModelProperty(value = "创建人userName")
    private String createUserName;

    @ApiModelProperty("主部门名称")
    private String mainDepartmentName;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "启用状态 0：关闭，1：启用")
    private Integer isOpen;

    @ApiModelProperty(value = "sop类型 0：定时sop，1：循环sop，2：新客sop，3：活动sop，4：生日sop，5：群日历")
    private Integer sopType;

    @ApiModelProperty(value = "使用群聊类型 0：指定群聊 ,1：筛选群聊")
    private Integer filterType;

}
