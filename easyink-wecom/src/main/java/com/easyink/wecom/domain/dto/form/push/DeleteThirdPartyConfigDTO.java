package com.easyink.wecom.domain.dto.form.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 删除企业第三方服务推送配置DTO
 *
 * @author easyink
 * @date 2024-01-01
 */
@ApiModel("企业第三方服务推送配置DTO")
@Data
public class DeleteThirdPartyConfigDTO {


    @ApiModelProperty("企业id列表")
    private List<String> corpIdList;

}
