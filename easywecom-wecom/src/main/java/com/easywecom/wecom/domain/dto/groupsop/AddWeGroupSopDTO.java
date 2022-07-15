package com.easywecom.wecom.domain.dto.groupsop;

import com.easywecom.wecom.domain.WeOperationsCenterSopEntity;
import com.easywecom.wecom.domain.dto.customersop.AddWeCustomerSopDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：AddWeGroupSopDTO
 *
 * @author Society my sister Li
 * @date 2021-11-30 15:29
 */
@Data
@ApiModel("群/客户SOP基本信息")
public class AddWeGroupSopDTO extends WeOperationsCenterSopEntity {

    @ApiModelProperty("指定群聊的群聊chatIdList")
    private List<String> chatIdList;

    @ApiModelProperty("SOP规则")
    private List<AddWeOperationsCenterSopRuleDTO> ruleList;

    @ApiModelProperty("筛选群聊的条件")
    private AddGroupSopFilterDTO sopFilter;
    /**
     * 客户sop信息
     */
    @ApiModelProperty("指定员工id")
    private List<String> userIdList;

    @ApiModelProperty("指定部门id")
    private List<String> departmentIdList;

    @ApiModelProperty("筛选客户的条件")
    private AddWeCustomerSopDTO sopCustomerFilter;
}
