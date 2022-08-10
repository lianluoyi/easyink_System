package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 类名: 批量编辑客户自定义字段请求参数
 *
 * @author : silver_chariot
 * @date : 2021/11/15 9:15
 */
@ApiModel("批量编辑客户自定义字段请求参数")
@Data
public class BatchSaveCustomerExtendPropertyDTO {

    @ApiModelProperty(value = "需要编辑的客户自定义字段集合")
    @NotEmpty(message = "自定义字段集合不能为空")
    private List<SaveCustomerExtendPropertyDTO> properties;
    /**
     * 企业ID
     */
    private String corpId;
}
