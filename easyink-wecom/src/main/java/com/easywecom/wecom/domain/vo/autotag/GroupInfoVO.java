package com.easywecom.wecom.domain.vo.autotag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 群详情VO
 *
 * @author tigger
 * 2022/3/1 17:44
 **/
@Data
public class GroupInfoVO {

    @ApiModelProperty("群id")
    private String chatId;
    @ApiModelProperty("群名称")
    private String groupName;
}
