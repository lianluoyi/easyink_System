package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 类名： WeWordsDelDTO
 *
 * @author 佚名
 * @date 2021/10/28 15:05
 */
@ApiModel("话术库删除DTO")
@Data
public class WeWordsDelDTO {
    @ApiModelProperty("话术库列表id")
    @NotEmpty(message = "话术库列表id不能为空")
    private List<Long> ids;
}
