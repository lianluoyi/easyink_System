package com.easyink.wecom.openapi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 类名: app开发参数实体
 *
 * @author : silver_chariot
 * @date : 2022/3/14 14:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppIdInfo {
    @TableField("corp_id")
    @ApiModelProperty(value = "企业ID ")
    private String corpId;

    @TableField("app_id")
    @ApiModelProperty(value = "app_id ")
    private String appId;

    @TableField("app_secret")
    @ApiModelProperty(value = "app_secret ")
    private String appSecret;


    @TableField("create_time")
    @ApiModelProperty(value = "创建时间 ")
    private Date createTime;


    @TableField("update_time")
    @ApiModelProperty(value = "更新时间 ")
    private Date updateTime;
}
