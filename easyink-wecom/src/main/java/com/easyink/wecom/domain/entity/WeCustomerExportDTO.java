package com.easyink.wecom.domain.entity;

import com.easyink.wecom.domain.WeCustomer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 类名: 导出客户列表请求参数
 *
 * @author : silver_chariot
 * @date : 2021/11/16 16:01
 */
@Data
@ApiModel("导出企业微信客户列表参数")
public class WeCustomerExportDTO extends WeCustomer {

    @NotEmpty(message = "请选择需要导出的字段")
    @ApiModelProperty(value = "指定导出的字段名集合")
    private List<String> selectedProperties;
}
