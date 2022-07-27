package com.easywecom.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * ClassName： DeleteRadarChannelDTO
 *
 * @author wx
 * @date 2022/7/19 17:23
 */
@Data
@ApiModel("雷达渠道批量删除DTO")
public class DeleteRadarChannelDTO {
    @NotEmpty(message = "请至少选择一个删除的目标")
    @ApiModelProperty("渠道id序列")
    private List<Long> idList;
}
