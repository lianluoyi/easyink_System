package com.easyink.wecom.domain.entity.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 权限
 *
 * @author 佚名
 * @date 2022/1/7 10:05
 */
@ApiModel("权限")
@Data
public class Privilege {
    @ApiModelProperty("允许查看成员的全部客户朋友圈发表")
    private boolean view_moment_list;
    @ApiModelProperty("允许成员发表客户朋友圈，默认为true")
    private boolean send_moment;
    @ApiModelProperty("配置封面和签名，默认为true")
    private boolean manage_moment_cover_and_sign;
}
