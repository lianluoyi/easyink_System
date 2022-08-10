package com.easyink.wecom.domain.vo.sop;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名： GroupSopVO
 *
 * @author 佚名
 * @date 2021/12/2 21:43
 */
@ApiModel("sop群VO GroupSopVO")
@Data
public class GroupSopVO {
    @ApiModelProperty(value = "群名")
    private String groupName;

    @ApiModelProperty(value = "群主userId")
    private String owner;

    @ApiModelProperty(value = "群主姓名")
    private String userName;

    @ApiModelProperty(value = "主部门名")
    private String mainDepartmentName;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("加入sop时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;

    @ApiModelProperty("群ID")
    private String chatId;

}
