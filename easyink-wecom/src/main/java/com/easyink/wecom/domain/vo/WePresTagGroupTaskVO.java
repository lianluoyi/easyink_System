package com.easyink.wecom.domain.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.enums.CommunityTaskType;
import com.easyink.wecom.domain.WeTag;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 类名：老客标签建群任务Vo
 *
 * @author Society my sister Li
 * @date 2021-10-18 16:46
 */
@Data
@ApiModel("标签入群任务实体")
public class WePresTagGroupTaskVO {

    @TableField(exist = false)
    @ApiModelProperty("类型。该属性仅用于H5页面与SOP混合列表的任务类型判断")
    private final Integer type = CommunityTaskType.TAG.getType();

    @ApiModelProperty("老客标签建群任务id")
    private Long taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("加群引导语")
    private String welcomeMsg;

    @JsonIgnore
    @ApiModelProperty("群活码id")
    private Long codeId;

    @JsonIgnore
    @ApiModelProperty("群活码连接")
    private String codeUrl;

    @JsonIgnore
    @ApiModelProperty("群活码名")
    private String activityName;

    @JsonIgnore
    @ApiModelProperty("群活码描述")
    private String activityDesc;

    @JsonIgnore
    @ApiModelProperty("创建类型 1:群二维码 2: 企微活码")
    private Integer createType;

    @TableField(exist = false)
    @ApiModelProperty("群活码详情")
    private WeGroupCodeVO groupCodeInfo;

    @ApiModelProperty("发送方式 0: 企业群发 1：个人群发")
    private Integer sendType;

    @ApiModelProperty("当前群人数")
    private Integer totalMember;

    @ApiModelProperty("使用员工列表")
    @TableField(exist = false)
    private List<WeCommunityTaskEmplVO> scopeList;

    @ApiModelProperty("标签列表")
    @TableField(exist = false)
    private List<WeTag> tagList;

    @ApiModelProperty("发送范围 0: 全部客户 1：部分客户")
    private Integer sendScope;

    @ApiModelProperty("发送性别 0: 全部 1： 男 2： 女 3：未知")
    private Integer sendGender;

    @ApiModelProperty("目标客户被添加起始时间")
    private String cusBeginTime;

    @ApiModelProperty("目标客户被添加结束时间")
    private String cusEndTime;

    @ApiModelProperty("企业群发消息ID")
    private String msgid;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    @ApiModelProperty("创建人部门名称")
    private String mainDepartmentName;
    /**
     * 群活码id
     */
    private String groupCodeId;

    /**
     * 设置群活码信息
     */
    public void fillGroupCodeVo() {
        WeGroupCodeVO groupCodeVo = new WeGroupCodeVO(this.getCodeId(), this.getCodeUrl(), this.getActivityName(), this.getActivityDesc(), this.getCreateType());
        this.setGroupCodeInfo(groupCodeVo);
    }
}
