package com.easywecom.wecom.domain.vo.wegrouptag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：WeGroupTagCategoryVO
 *
 * @author Society my sister Li
 * @date 2021-11-12 17:05
 */
@Data
@ApiModel("分页查询客户群标签组数据")
public class PageWeGroupTagCategoryVO {

    @ApiModelProperty("标签组名称")
    private String groupName;

    @ApiModelProperty("标签组ID")
    private Long groupId;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String corpId;

    @ApiModelProperty("标签列表")
    private List<PageWeGroupTagVO> weTags;
}
