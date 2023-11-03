package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


/**
 * 欢迎语模板对象 we_msg_tlp
 *
 * @author 佚名
 * @date 2021-7-29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("we_msg_tlp")
@ApiModel("欢迎语模板对象")
public class WeMsgTlp {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id", hidden = true)
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "授权企业ID", hidden = true)
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "默认欢迎语")
    @TableField("default_welcome_msg")
    private String defaultWelcomeMsg;

    @ApiModelProperty(value = "欢迎语适用对象类型:1:员工欢迎语;2:客户群欢迎语", hidden = true)
    @TableField("welcome_msg_tpl_type")
    private Integer welcomeMsgTplType;

    @ApiModelProperty(value = "是否存在有特殊时段欢迎语(存在则有关联rule_id) 0:否 1:是", hidden = true)
    @TableField("exist_special_flag")
    private Boolean existSpecialFlag;

    @ApiModelProperty(value = "是否存在有过滤条件（存在则关联we_msg_tlp_filter表）0：不存在，1：存在")
    @TableField("exist_filter_flag")
    private Boolean existFilterFlag;

    @ApiModelProperty(value = "多个筛选条件间的关联，0：或；1：且（仅存在过滤条件有效）")
    @TableField("multi_filter_association")
    private Integer multiFilterAssociation;

    @ApiModelProperty(value = "入群欢迎语返回的模板id", hidden = true)
    @TableField("template_id")
    private String templateId;

    @ApiModelProperty(value = "群素材是否通知员工标识(0: 不通知(默认) 1:通知)")
    @TableField("notice_flag")
    private boolean noticeFlag;

    /**
     * 创建者
     */
    @ApiModelProperty(hidden = true)
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(hidden = true)
    @TableField("create_time")
    private Date createTime = new Date();

    @TableField(exist = false)
    @ApiModelProperty("默认欢迎语模板素材")
    private List<WeMsgTlpMaterial> defaultMaterialList;

    @TableField(exist = false)
    @ApiModelProperty("创建人姓名")
    private String createName;

    @TableField(exist = false)
    @ApiModelProperty("部门名称")
    private String mainDepartmentName;
}
