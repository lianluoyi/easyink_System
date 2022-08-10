package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 类名：ReleaseNotesVO
 *
 * @author Society my sister Li
 * @date 2022-01-07 14:14
 */
@Data
@Builder
@ApiModel("最新版本及说明")
public class ReleaseNotesVO {

    @ApiModelProperty(value = "当前版本号")
    private String version;

    @ApiModelProperty(value = "版本说明")
    private String notes;
}
