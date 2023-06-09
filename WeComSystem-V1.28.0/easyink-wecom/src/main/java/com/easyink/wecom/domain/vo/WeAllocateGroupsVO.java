package com.easyink.wecom.domain.vo;

import com.easyink.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名： WeAllocateGroupsVO
 *
 * @author 佚名
 * @date 2021/8/31 10:03
 */
@Data
public class WeAllocateGroupsVO extends BaseEntity {
    /**
     * 群id
     */
    @ApiModelProperty("群聊id")
    private String chatId;

    /**
     * 群名称
     */
    @ApiModelProperty("群名称")
    private String groupName;

    /**
     * 公司id
     */
    private String corpId;

    /**
     * 群聊继承状态
     */
    private Integer status;

    /**
     * 群客户数量
     */
    @ApiModelProperty("群客户数量")
    private Integer memberNum;

    /**
     * 群主所在部门主部门
     */
    @ApiModelProperty("主部门名")
    private String mainDepartmentName;

    /**
     * 分配时间
     */
    @ApiModelProperty("分配时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date allocateTime;

    /**
     * 新群主名称
     */
    @ApiModelProperty("新群主名")
    private String newOwnerName;

    /**
     * 原群主id
     */
    @ApiModelProperty(value = "原群主id")
    private String oldOwner;

    @ApiModelProperty("分配结果")
    private String allocateResult;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    /**
     * 员工离职时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "离职时间")
    private Date dimissionTime;
}
