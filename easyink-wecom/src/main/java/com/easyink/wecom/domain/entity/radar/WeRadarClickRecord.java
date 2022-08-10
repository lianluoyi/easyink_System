package com.easyink.wecom.domain.entity.radar;

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
 * ClassName： WeRadarClickRecord
 *
 * @author wx
 * @date 2022/7/19 19:46
 */
@Data
@ApiModel("雷达点击实体")
@TableName("we_radar_click_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeRadarClickRecord {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("雷达点击记录表ID，主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "雷达Id", hidden = true)
    @TableField("radar_id")
    private Long radarId;

    @ApiModelProperty(value = "发送活码用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "发送雷达链接的用户名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "客户id")
    @TableField("external_user_id")
    private String externalUserId;

    @ApiModelProperty(value = "用户头像")
    @TableField("external_user_head_image")
    private String externalUserHeadImage;

    @ApiModelProperty(value = "客户名称")
    @TableField("external_user_name")
    private String externalUserName;

    @ApiModelProperty(value = "渠道类型（（0未知渠道,1员工活码，2朋友圈，3群发，4侧边栏,5欢迎语,6 客户SOP,7群SOP，8新客进群，9群日历）")
    @TableField("channel_type")
    private Integer channelType;

    @ApiModelProperty(value = "未知渠道 COMMENT 渠道名")
    @TableField("channel_name")
    private String channelName;

    @ApiModelProperty(value = "外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来")
    @TableField("union_id")
    private String unionId;

    @ApiModelProperty(value = "公众号/小程序open_id")
    @TableField("open_id")
    private String openId;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "创建日期（格式yyyy-mm-dd)")
    @TableField("create_date")
    private String createDate;
}
