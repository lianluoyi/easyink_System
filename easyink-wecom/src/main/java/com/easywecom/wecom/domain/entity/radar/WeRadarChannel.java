package com.easywecom.wecom.domain.entity.radar;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： WeRadarChannel
 *
 * @author wx
 * @date 2022/7/19 14:56
 */
@Data
@TableName("we_radar_channel")
@ApiModel("雷达渠道")
public class WeRadarChannel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "渠道id, 主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "雷达id")
    @TableField("radar_id")
    private Long radarId;

    @ApiModelProperty(value = "渠道名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "渠道的短链url")
    @TableField("short_url")
    private String shortUrl;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

}
