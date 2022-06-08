package com.easywecom.wecom.domain.dto.groupcode;

import com.easywecom.wecom.domain.WeGroupCodeActual;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 添加企业微信活码DTO
 *
 * @author tigger
 * 2022/2/11 13:52
 **/
@Data
public class AddCorpCodeDTO {
    @NotEmpty(message = "至少选择一个实际活码列表")
    @ApiModelProperty("企业微信实际活码列表")
    private List<WeGroupCodeActual> weGroupCodeCorpActualList;
    @ApiModelProperty("客户群活码id,在主表存在的时候必传")
    private Long groupCodeId;
}
