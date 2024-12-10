package com.easyink.wecom.domain.dto;

import com.easyink.common.core.domain.BaseEntity;
import com.easyink.wecom.annotation.Cipher;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：FindWeGroupDTO
 *
 * @author Society my sister Li
 * @date 2021-11-15 17:56
 */
@Data
@ApiModel("客户群的搜索条件实体")
public class FindWeGroupDTO extends BaseEntity {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty("群昵称（模糊搜索）")
    private String groupName;

    @ApiModelProperty("群主（模糊搜索）")
    private String groupLeader;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String beginTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endTime;

    @ApiModelProperty("群状态（-1:全部、0:正常、1:待继承）")
    private Integer groupStatus;

    @ApiModelProperty("标签列表（多个已逗号隔开）")
    private String tagIds;

    @ApiModelProperty("群ID")
    private String chatId;

    @ApiModelProperty("群主userId（多个已逗号隔开）")
    private String ownerIds;

    /**
     * 是否需要sql过滤标签, 默认true, 兼容之前的查询
     */
    private boolean needSqlFilterTag = true;
}
