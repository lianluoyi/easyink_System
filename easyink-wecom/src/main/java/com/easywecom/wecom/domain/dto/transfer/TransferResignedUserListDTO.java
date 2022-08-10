package com.easywecom.wecom.domain.dto.transfer;

import com.easywecom.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类名: 查看离职员工列表参数实体
 *
 * @author : silver_chariot
 * @date : 2021/12/3 16:51
 */
@Data
public class TransferResignedUserListDTO extends BaseEntity {

    @ApiModelProperty(value = "企业id,不必传")
    private String corpId;

    @ApiModelProperty("员工姓名")
    private String userName;

    @NotNull(message = "参数缺失")
    @ApiModelProperty(value = "离职是否分配(1:已分配;0:未分配)", required = true)
    private Integer isAllocate;

}
