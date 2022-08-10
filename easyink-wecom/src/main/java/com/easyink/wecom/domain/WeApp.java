package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 佚名
 * @description: 自建应用相关
 * @date 2021-7-28
 **/
@Data
@TableName("we_app")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "自建应用实体")
public class WeApp {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "应用id")
    @TableField("agent_id")
    private String agentId;

    @ApiModelProperty(value = "应用名称")
    @TableField("agent_name")
    private String agentName;

    @ApiModelProperty(value = "应用密钥")
    @TableField("agent_secret")
    private String agentSecret;

    @ApiModelProperty(value = "应用描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "应用图标")
    @TableField("square_logo_url")
    private String squareLogoUrl;

    @ApiModelProperty(value = "企业应用是否被停用(1:是;0:否)")
    @TableField("close")
    private Integer close;

    @ApiModelProperty(value = "企业应用可信域名")
    @TableField("redirect_domain")
    private String redirectDomain;

    @ApiModelProperty(value = "企业应用是否打开地理位置上报 0：不上报；1：进入会话上报；")
    @TableField("report_location_flag")
    private Integer reportLocationFlag;

    @ApiModelProperty(value = "是否上报用户进入应用事件。0：不接收；1：接收")
    @TableField("isreportenter")
    private Integer isreportenter;

    @ApiModelProperty(value = "应用主页url")
    @TableField("home_url")
    private String homeUrl;

    @ApiModelProperty(value = "应用类型(1:自建应用;)")
    @TableField("app_type")
    private Integer appType;

    @ApiModelProperty(value = "应用创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @TableField("del_flag")
    private String delFlag;

    @ApiModelProperty(value = "帐号状态（0正常 1停用)")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "可见部门")
    @TableField("allow_partys")
    private String allowPartys;

    @ApiModelProperty(value = "可见用户")
    @TableField("allow_userinfos")
    private String allowUserinfos;

    @TableField(exist = false)
    private String logoMediaid;

}
