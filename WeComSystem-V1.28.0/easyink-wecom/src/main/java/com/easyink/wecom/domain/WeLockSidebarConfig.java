package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 第三方SCRM系统侧边栏配置(WeLockSidebarConfig)实体类
 *
 * @author wx
 * @since 2023-03-14 15:39:08
 */
@Data
@NoArgsConstructor
@TableName(value = "we_lock_sidebar_config")
@AllArgsConstructor
public class WeLockSidebarConfig implements Serializable {
    private static final long serialVersionUID = -17500271871002077L;

    @ApiModelProperty("第三方SCRM系统app_id")
    @TableField("app_id")
    private String appId;

    @ApiModelProperty("第三方SCRM系统app_id")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("第三方SCRMapp_secret")
    @TableField("app_secret")
    private String appSecret;


}

