package com.easywecom.wecom.domain.dto.radar;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * ClassName： DeleteRadarDTO
 *
 * @author wx
 * @date 2022/7/18 19:57
 */

@Data
@ApiModel("雷达批量删除DTO")
public class DeleteRadarDTO {
    @NotEmpty(message = "请至少选择一个删除的目标")
    @ApiModelProperty("雷达id序列")
    private List<Long> idList;
}


