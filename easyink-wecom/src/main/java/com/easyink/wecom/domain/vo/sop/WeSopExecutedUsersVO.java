package com.easyink.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sop员工执行记录
 *
 * @author tigger
 * 2021/12/7 13:01
 **/
@Data
public class WeSopExecutedUsersVO extends AbstractExecuteVO{
    @ApiModelProperty("员工id")
    private String userId;
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("群总数")
    private String groupCount;
    @ApiModelProperty("员工头像")
    private String headImageUrl;
    @ApiModelProperty("公司名称")
    private String corpName;
}
