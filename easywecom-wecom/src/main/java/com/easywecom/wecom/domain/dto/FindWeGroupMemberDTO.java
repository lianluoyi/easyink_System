package com.easywecom.wecom.domain.dto;

import com.easywecom.wecom.domain.WeGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：FindWeGroupMemberDTO
 *
 * @author Society my sister Li
 * @date 2021-11-16 10:17
 */
@Data
@ApiModel("群成员搜索条件实体")
public class FindWeGroupMemberDTO extends WeGroupMember {

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
}
