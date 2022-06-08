package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 类名： 我的应用使用范围
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
@Data
@TableName("we_my_application_use_scope")
@ApiModel("我的应用使用范围实体")
public class WeMyApplicationUseScopeEntity {


    @ApiModelProperty(value = "ID")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "应用Id")
    @TableField("appid")
    private Integer appid;

    /**
     * 使用类型(1指定员工，2指定角色)
     */
    @ApiModelProperty(value = "使用类型(1指定员工，2指定角色)")
    @TableField("type")
    private Integer type;
    /**
     * 指定员工存userId,指定角色存角色ID
     */
    @ApiModelProperty(value = "指定员工存userId,指定角色存角色ID")
    @TableField("val")
    private String val;

}
