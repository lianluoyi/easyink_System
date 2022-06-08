package com.easywecom.wecom.domain.dto.wegrouptag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：DelWeGroupTagCategoryDTO
 *
 * @author Society my sister Li
 * @date 2021-11-12 15:51
 */
@Data
@ApiModel("删除群标签组")
public class DelWeGroupTagCategoryDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty("删除的标签组列表")
    private List<Long> delList;
}
