package com.easywecom.wecom.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easywecom.wecom.domain.WeEmpleCodeTag;
import com.easywecom.wecom.domain.WeEmpleCodeUseScop;
import com.easywecom.wecom.domain.WeGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 社区运营 新客自动拉群
 */
@ApiModel("新客建群VO 《WeCommunityNewGroupVO》")
@Data
public class WeCommunityNewGroupVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("创建人主部门（admin为空字符串）")
    private String departmentName;
    /**
     * 活码名称
     */
    @ApiModelProperty("活码名称")
    private String codeName;

    @JsonIgnore
    private Long groupCodeId;

    @JsonIgnore
    private Long emplCodeId;

    /**
     * 员工活码URL
     */
    @ApiModelProperty("员工活码URL")
    private String emplCodeUrl;

    /**
     * 欢迎语(即员工活码设定的欢迎语)
     */
    @TableField(exist = false)
    @ApiModelProperty("欢迎语(即员工活码设定的欢迎语)")
    private String welcomeMsg;

    /**
     * 群活码信息
     */
    @TableField(exist = false)
    private WeGroupCodeVO groupCodeInfo;

    /**
     * 员工信息
     */
    @TableField(exist = false)
    private List<WeEmpleCodeUseScop> emplList;

    /**
     * 对应群聊信息
     */
    @TableField(exist = false)
    private List<WeGroup> groupList;

    /**
     * 客户标签列表
     */
    @TableField(exist = false)
    private List<WeEmpleCodeTag> tagList;

    /**
     * 当前添加的好友数
     */
    @TableField(exist = false)
    private Integer cusNumber = 0;

    /**
     * 是否跳过验证
     */
    @TableField(exist = false)
    private Integer skipVerify;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

}
