package com.easyink.wecom.domain.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDecryptDTO {

    @NotEmpty(message = "待解密值列表不能为空")
    @ApiModelProperty(value = "待解密值列表")
    private List<String> encryptValueList;
}