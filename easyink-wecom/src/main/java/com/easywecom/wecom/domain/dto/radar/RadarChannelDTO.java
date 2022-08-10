package com.easywecom.wecom.domain.dto.radar;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.entity.radar.WeRadarChannel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ClassName： RadarChannelDTO
 *
 * @author wx
 * @date 2022/7/19 16:11
 */
@Data
@ApiModel("雷达渠道DTO")
public class RadarChannelDTO {

    @ApiModelProperty(value = "渠道id, 主键id")
    private Long id;

    @ApiModelProperty(value = "雷达id")
    @NotNull
    private Long radarId;

    @ApiModelProperty(value = "渠道名称")
    @NotBlank(message = "请填写渠道名称")
    @Size(max = 32, message = "渠道名称，最长32字符")
    private String name;

    @ApiModelProperty(value = "渠道的短链url")
    private String shortUrl;

    public WeRadarChannel buildWeRadarChannel() {
        WeRadarChannel radarChannel = new WeRadarChannel();
        BeanUtils.copyProperties(this, radarChannel);
        return radarChannel;
    }
}
