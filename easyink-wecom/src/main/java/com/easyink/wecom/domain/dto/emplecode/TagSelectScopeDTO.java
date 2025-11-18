package com.easyink.wecom.domain.dto.emplecode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 标签可选范围DTO
 *
 * @author tigger
 * 2025/4/29 13:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "专属活码设置可选标签DTO")
public class TagSelectScopeDTO {

    /**
     * 活码id
     */
    @ApiModelProperty("活码id")
    @NotBlank(message = "所属活码id不能为空")
    private String originEmpleId;

    /**
     * 选择的标签分组id, 可选
     */
    private List<String> selectGroupIdList;

    /**
     * 选择的标签id列表
     */
    private List<String> selectTagIdList;

}
