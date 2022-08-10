package com.easyink.wecom.domain.vo.sop;

import com.easyink.wecom.domain.WeOperationsCenterSopRulesEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： SopRuleVO
 *
 * @author 佚名
 * @date 2021/12/3 9:49
 */
@Data
@ApiModel("sop规则VO SopRuleVO")
public class SopRuleVO extends WeOperationsCenterSopRulesEntity {
    @ApiModelProperty(value = "规则附件")
    private List<SopAttachmentVO> materialList;
}
