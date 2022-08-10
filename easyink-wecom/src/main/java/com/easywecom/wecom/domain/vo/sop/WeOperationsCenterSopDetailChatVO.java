package com.easywecom.wecom.domain.vo.sop;

import com.easywecom.wecom.domain.vo.sop.abs.AbstractWeOperationsCenterSopDetailVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sop群执行详情VO
 *
 * @author tigger
 * 2021/12/10 14:13
 **/
@Data
public class WeOperationsCenterSopDetailChatVO extends AbstractWeOperationsCenterSopDetailVO {

    @ApiModelProperty("群名称")
    private String chatName;
    @ApiModelProperty("员工名称")
    private String chatCreatorName;
    @ApiModelProperty("规则名称")
    private String ruleName;
}
