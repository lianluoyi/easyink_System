package com.easywecom.wecom.domain.dto;

import com.easywecom.wecom.domain.WeGroupCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：FindWeGroupCodeDTO
 *
 * @author Society my sister Li
 * @date 2021-10-26 17:04
 */
@Data
@ApiModel("查询客户群活码实体")
public class FindWeGroupCodeDTO extends WeGroupCode {

    @ApiModelProperty("开始时间")
    private String beginTime;

    @ApiModelProperty("结束时间")
    private String endTime;
}
