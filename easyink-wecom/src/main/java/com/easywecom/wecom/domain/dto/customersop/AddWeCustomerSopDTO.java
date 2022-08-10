package com.easywecom.wecom.domain.dto.customersop;

import com.easywecom.wecom.domain.WeOperationsCenterCustomerSopFilterEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 添加客户sop DTO
 *
 * @author 佚名
 * @date 2021/12/1 17:24
 */
@Data
@ApiModel("添加客户sop DTO <AddWeCustomerSopDTO>")
public class AddWeCustomerSopDTO extends WeOperationsCenterCustomerSopFilterEntity {
    @ApiModelProperty("满足的客户属性值")
    private List<Column> columnList;
}
