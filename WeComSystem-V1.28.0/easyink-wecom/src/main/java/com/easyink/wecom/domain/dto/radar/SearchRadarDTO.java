package com.easyink.wecom.domain.dto.radar;

import com.easyink.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * ClassName： SearchRadarDTO
 *
 * @author wx
 * @date 2022/7/19 11:42
 */
@Data
@ApiModel("搜索雷达DTO")
@EqualsAndHashCode(callSuper = true)
public class SearchRadarDTO extends BaseEntity {

    @ApiModelProperty("雷达/链接标题")
    private String searchTitle;

    @ApiModelProperty(value = "雷达类型（1个人雷达，2部门雷达，3企业雷达）")
    private Integer type;

    @ApiModelProperty(value = "员工idList")
    @JsonIgnore
    private List<String> userIds;

    @ApiModelProperty(value = "corpId,同时代表企业雷达传值")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty("排序按钮 true:正序 ,false：逆序")
    private Boolean enableSort;
}
