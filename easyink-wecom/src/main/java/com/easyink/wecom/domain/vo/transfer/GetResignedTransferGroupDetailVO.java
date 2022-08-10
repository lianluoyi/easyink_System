package com.easyink.wecom.domain.vo.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名: 获取离职分配客户群详情实体
 *
 * @author : silver_chariot
 * @date : 2021/12/9 10:52
 */
@Data
public class GetResignedTransferGroupDetailVO {

    @ApiModelProperty("群聊id")
    private String chatId;

    @ApiModelProperty("群名称")
    private String groupName;

    private String corpId;


    private Integer status;

    @ApiModelProperty("群客户数量")
    private Integer memberNum;

    @ApiModelProperty("主部门名")
    private String mainDepartmentName;

    @ApiModelProperty("分配时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date allocateTime;

    @ApiModelProperty("新群主名")
    private String newOwnerName;

    @ApiModelProperty(value = "原群主id")
    private String oldOwner;

    @ApiModelProperty(value = "旧群主名称")
    private String oldOwnerName;

    @ApiModelProperty("分配结果")
    private String allocateResult;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "离职时间")
    private Date dimissionTime;
}
