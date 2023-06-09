package com.easyink.wecom.domain.entity.radar;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName： WeRadarTag
 *
 * @author wx
 * @date 2022/7/18 17:23
 */
@Data
@TableName("we_radar")
@ApiModel("客户标签")
public class WeRadarTag {
    @ApiModelProperty("雷达id")
    @TableField("radar_id")
    @JsonIgnore
    private Long radarId;

    @ApiModelProperty("标签id")
    @TableField("tag_id")
    private String tagId;

    @ApiModelProperty("标签名")
    private String tagName;
}
