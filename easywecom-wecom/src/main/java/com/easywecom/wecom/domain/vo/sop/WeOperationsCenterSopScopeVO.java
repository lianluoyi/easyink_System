package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：WeOperationsCenterSopScopeVO
 *
 * @author Society my sister Li
 * @date 2021-12-01 18:42
 */
@Data
@ApiModel("使用员工/群聊")
public class WeOperationsCenterSopScopeVO {

    @ApiModelProperty("使用者数据ID")
    private Long id;

    @ApiModelProperty("作用者userId")
    private String targetId;

    @ApiModelProperty("作用者昵称")
    private String userName;
}
