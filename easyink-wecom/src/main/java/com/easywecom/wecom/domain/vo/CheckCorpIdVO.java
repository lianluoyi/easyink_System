package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： CheckCorpIdVO
 *
 * @author 佚名
 * @date 2022/1/4 16:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("企业id是否为待开发应用")
public class CheckCorpIdVO {
    @ApiModelProperty("是否为待开发应用")
    private boolean dkCorp;
}
