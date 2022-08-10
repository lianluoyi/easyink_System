package com.easywecom.wecom.domain.dto.wegrouptag;

import com.easywecom.wecom.domain.WeGroupTag;
import com.easywecom.wecom.domain.WeGroupTagCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：UpdateWeGroupTagCategoryDTO
 *
 * @author Society my sister Li
 * @date 2021-11-12 15:51
 */
@Data
@ApiModel("编辑群标签组")
public class UpdateWeGroupTagCategoryDTO extends WeGroupTagCategory {

    @ApiModelProperty("新增的标签列表")
    private List<WeGroupTag> addList;

    @ApiModelProperty("删除的标签列表")
    private List<Long> delList;
}
