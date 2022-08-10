package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 类名: WeApplicationCenter
 *
 * @author: 1*+
 * @date: 2021-10-14 16:01
 */
@Data
@TableName("we_application_center")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "应用中心实体")
public class WeApplicationCenter implements Serializable {


    @ApiModelProperty(value = "应用ID")
    @TableId(type = IdType.AUTO)
    @TableField("appid")
    private Integer appid;

    @ApiModelProperty(value = "应用名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "应用描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "应用头像")
    @TableField("logo_url")
    private String logoUrl;

    @ApiModelProperty(value = "应用类型(1:企业工具，2:客户资源，3:内容资源)")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "功能介绍")
    @TableField("introduction")
    private String introduction;

    @ApiModelProperty(value = "使用说明")
    @TableField("instructions")
    private String instructions;

    @ApiModelProperty(value = "咨询服务")
    @TableField("consulting_service")
    private String consultingService;

    @ApiModelProperty(value = "启用(ON1Y)")
    @TableField("enable")
    private Boolean enable;

    @ApiModelProperty(value = "上架时间")
    @TableField("create_time")
    private Date createTime;

    /**
     * 开发类型（1自研，2三方）
     */
    @ApiModelProperty(value = "开发类型（1自研，2三方）")
    @TableField("development_type")
    private Integer developmentType;
    /**
     * 侧边栏url:自研存储相对路径，三方开发存储完整url
     */
    @ApiModelProperty(value = "侧边栏url:自研存储相对路径，三方开发存储完整url")
    @TableField("sidebar_redirect_url")
    private String sidebarRedirectUrl;
    /**
     * 应用入口url
     */
    @ApiModelProperty(value = "应用入口url")
    @TableField("application_entrance_url")
    private String applicationEntranceUrl;
}
