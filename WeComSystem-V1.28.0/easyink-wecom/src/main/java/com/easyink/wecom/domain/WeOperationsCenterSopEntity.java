package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;


/**
 * 类名： SOP基本信息
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Data
@TableName("we_operations_center_sop")
@ApiModel("SOP基本信息实体")
public class WeOperationsCenterSopEntity {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID",hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;
    /**
     * SOP名称
     */
    @ApiModelProperty(value = "SOP名称",required = true)
    @TableField("name")
    @NotBlank(message = "name不能为空")
    private String name;
    /**
     * 创建人.员工userId
     */
    @ApiModelProperty(value = "创建人.员工userId",hidden = true)
    @TableField("create_by")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间",hidden = true)
    @TableField("create_time")
    private Date createTime;
    /**
     * sop类型 0：定时sop，1：循环sop，2：新客sop，3：活动sop，4：生日sop，5：群日历
     */
    @ApiModelProperty(value = "sop类型 0：定时sop，1：循环sop，2：新客sop，3：活动sop，4：生日sop，5：群日历")
    @TableField("sop_type")
    private Integer sopType;
    /**
     * 使用群聊类型 0：指定群聊 ,1：筛选群聊
     */
    @ApiModelProperty(value = "使用群聊类型 0：指定群聊 ,1：筛选群聊 ")
    @TableField("filter_type")
    private Integer filterType;
    /**
     * 启用状态 0：关闭，1：启用
     */
    @ApiModelProperty(value = "启用状态 0：关闭，1：启用")
    @TableField("is_open")
    private Boolean isOpen = true;

}
