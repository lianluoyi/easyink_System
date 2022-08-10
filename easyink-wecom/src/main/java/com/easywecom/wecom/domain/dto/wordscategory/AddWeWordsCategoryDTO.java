package com.easywecom.wecom.domain.dto.wordscategory;

import com.easywecom.wecom.domain.WeWordsCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：UpdateWeWordsCategoryDTO
 *
 * @author Society my sister Li
 * @date 2021-10-27 16:57
 */
@Data
@ApiModel("新增话术文件夹")
public class AddWeWordsCategoryDTO extends WeWordsCategory {

    @ApiModelProperty(value = "下级文件夹ID列表")
    private List<WeWordsCategoryChildSortDTO> childIdList;

}
