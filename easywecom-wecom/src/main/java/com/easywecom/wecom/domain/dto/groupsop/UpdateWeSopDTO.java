package com.easywecom.wecom.domain.dto.groupsop;

import com.easywecom.wecom.domain.WeOperationsCenterSopEntity;
import com.easywecom.wecom.domain.dto.customersop.AddWeCustomerSopDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 类名：UpdateWeSopDTO
 *
 * @author Society my sister Li
 * @date 2021-11-30 15:29
 */
@Data
@ApiModel("修改SOP")
public class UpdateWeSopDTO extends WeOperationsCenterSopEntity {

    @ApiModelProperty("指定群聊的群聊chatIdList")
    private List<String> chatIdList;

    @ApiModelProperty("SOP规则")
    private List<AddWeOperationsCenterSopRuleDTO> ruleList;

    @ApiModelProperty("筛选群聊的条件")
    private AddGroupSopFilterDTO sopFilter;

    @ApiModelProperty("需要删除的规则ID")
    private List<Long> delRuleList;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonIgnore
    private Date createTime;
    /**
     * 客户sop信息
     */
    @ApiModelProperty("指定员工id")
    private List<String> userIdList;

    @ApiModelProperty("筛选客户的条件")
    private AddWeCustomerSopDTO sopCustomerFilter;

}
