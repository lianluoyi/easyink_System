package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: PresTagExpectedReceptionVO
 *
 * @author: 1*+
 * @date: 2021-11-02 18:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("预计发送结果实体")
public class PresTagExpectedReceptionVO {

    @ApiModelProperty("预计发送客户数量")
    private Integer expectedCustomerCount;
    @ApiModelProperty("预计执行员工数量")
    private Integer expectedUserCount;


}
