package com.easyink.wecom.domain.dto.emplecode;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 新增自定义渠道DTO
 *
 * @author lichaoyu
 * @date 2023/8/15 17:58
 */
@Data
@ApiModel("新增自定义渠道")
public class AddCustomChannelDTO extends BaseEntity {

    @ApiModelProperty("自定义渠道名称")
    private String name;

    @ApiModelProperty("获客链接id")
    private Long empleCodeId;

    @ApiModelProperty("企业ID")
    private String corpId;

    @ApiModelProperty("默认渠道Url")
    private String defaultUrl;

    @ApiModelProperty("是否过滤默认渠道信息，True：过滤，False:不过滤")
    private Boolean isFilterDefaultUrl;

}
