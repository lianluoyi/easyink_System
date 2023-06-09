package com.easyink.wecom.domain.dto.message;

import com.easyink.wecom.domain.vo.radar.WeRadarVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： RadarMessageDTO
 *
 * @author wx
 * @date 2022/7/23 9:54
 */
@Data
@ApiModel("雷达链接DTO 《RadarMessageDTO》")
@NoArgsConstructor
public class RadarMessageDTO {

    @ApiModelProperty("雷达id")
    private Long radarId;

    @ApiModelProperty("雷达VO")
    private WeRadarVO radar;

    @ApiModelProperty("雷达标题")
    private String linkTitle;


    public RadarMessageDTO(Long radarId) {
        this.radarId = radarId;

    }
}
