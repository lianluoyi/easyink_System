package com.easyink.wecom.domain.dto.groupsop;

import com.easyink.wecom.domain.WeOperationsCenterSopRulesEntity;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：AddWeOperationsCenterSopRuleDTO
 *
 * @author Society my sister Li
 * @date 2021-11-30 15:39
 */
@Data
@ApiModel("添加素材")
public class AddWeOperationsCenterSopRuleDTO extends WeOperationsCenterSopRulesEntity {

    @ApiModelProperty("素材内容")
    private List<WeWordsDetailEntity> materialList;

    @ApiModelProperty("删除素材列表")
    private List<Long> delMaterialList;
}
