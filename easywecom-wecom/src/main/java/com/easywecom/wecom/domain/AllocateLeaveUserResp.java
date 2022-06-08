package com.easywecom.wecom.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 类名： AllocateLeaveUserRESP
 *
 * @author 佚名
 * @date 2021/10/8 19:15
 */
@Data
@Builder
@AllArgsConstructor
@ApiModel("离职分配结果")
public class AllocateLeaveUserResp {

    @ApiModelProperty("客户分配成功数量")
    private Integer weCustomerSucceedNum;

    @ApiModelProperty("客户分配失败数量")
    private Integer weCustomerFailNum;

    @ApiModelProperty("客户群分配成功数量")
    private Integer weGroupSucceedNum;

    @ApiModelProperty("客户群分配失败数量")
    private Integer weGroupFailNum;

    @ApiModelProperty("员工客户群今日分配已达上限员工列表")
    private List<String> list;

    public AllocateLeaveUserResp(AllocateWeCustomerResp allocateWeCustomerRESP, AllocateWeGroupResp resp) {
        this.weCustomerFailNum = allocateWeCustomerRESP.getFailNum();
        this.weCustomerSucceedNum = allocateWeCustomerRESP.getSucceedNum();
        this.weGroupFailNum = resp.getFailNum();
        this.weGroupSucceedNum = resp.getSucceedNum();
        this.list = resp.getList();
    }
}
