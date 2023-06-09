package com.easyink.wecom.domain.entity.radar;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： WeRadar
 *
 * @author wx
 * @date 2022/7/18 14:59
 */
@Data
@TableName("we_radar")
@ApiModel("雷达")
public class WeRadar {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "雷达id, 主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "授权企业ID", hidden = true)
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "雷达类型（1个人雷达，2部门雷达，3企业雷达）")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "雷达标题")
    @TableField("radar_title")
    private String radarTitle;

    @ApiModelProperty(value = "雷达原始路径url")
    @TableField("url")
    private String url;

    @ApiModelProperty(value = "雷达链接封面图")
    @TableField("cover_url")
    private String coverUrl;

    @ApiModelProperty(value = "链接标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "雷达链接摘要")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "链接时使用：0 默认，1 自定义")
    @TableField("is_defined")
    private Boolean isDefined;

    @ApiModelProperty(value = "是否开启行为通知（1[true]是0[false]否）")
    @TableField("enable_click_notice")
    private Boolean enableClickNotice;

    @ApiModelProperty(value = "是否允许轨迹记录（1[true]是 0[false]否)")
    @TableField("enable_behavior_record")
    private Boolean enableBehaviorRecord;

    @ApiModelProperty(value = "是否允许打上客户标签（ 1[true]是 0[false]否)")
    @TableField("enable_customer_tag")
    private Boolean enableCustomerTag;

    @ApiModelProperty(value = "更新后是否通知员工（true[1]是 false[0]否)")
    @TableField("enable_update_notice")
    private Boolean enableUpdateNotice;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private String updateTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

}
