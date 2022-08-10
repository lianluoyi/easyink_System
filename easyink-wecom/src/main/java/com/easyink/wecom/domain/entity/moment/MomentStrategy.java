package com.easyink.wecom.domain.entity.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 规则组
 * <p>
 * 如果规则组具有父规则组则其管理范围必须是父规则组的子集。
 *
 * @author 佚名
 * @date 2022/1/7 9:41
 */
@ApiModel("规则组")
@Data
public class MomentStrategy {
    @ApiModelProperty("规则组id")
    private Integer strategy_id;
    @ApiModelProperty("父规则组id， 如果当前规则组没父规则组，则为0")
    private Integer parent_id;
    @ApiModelProperty("规则组名称")
    private String strategy_name;
    @ApiModelProperty("规则组创建时间戳")
    private Long create_time;
    @ApiModelProperty("规则组管理员userid列表")
    private List<String> admin_list;
    @ApiModelProperty("权限")
    private Privilege privilege;
}
