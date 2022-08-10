package com.easyink.wecom.domain;

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
 * 类名: WeMyApplication
 *
 * @author: 1*+
 * @date: 2021-10-14 16:09
 */
@Data
@TableName("we_my_application")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "我的应用实体")
public class WeMyApplication implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    @TableField("id")
    private Integer id;

    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "应用ID")
    @TableField("appid")
    private Integer appid;

    @ApiModelProperty(value = "应用配置")
    @TableField("config")
    private String config;

    @ApiModelProperty(value = "启用(ON1Y)")
    @TableField("enable")
    private Boolean enable;

    @ApiModelProperty(value = "安装时间")
    @TableField("install_time")
    private Date installTime;

    @ApiModelProperty(value = "过期时间")
    @TableField("expire_time")
    private Date expireTime;

}
