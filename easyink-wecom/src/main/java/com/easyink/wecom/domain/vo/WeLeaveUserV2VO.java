package com.easyink.wecom.domain.vo;

import com.easyink.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 类名： WeLeaveUserTestVo
 *
 * @author 佚名
 * @date 2021/9/7 13:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("员工离职实体类")
public class WeLeaveUserV2VO extends BaseEntity {
    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("头像")
    private String headImageUrl;

    /**
     * 公司id
     */
    private String corpId;

    @ApiModelProperty("部门")
    private String mainDepartmentName;

    @ApiModelProperty("昵称")
    private String alias;

    @ApiModelProperty("离职时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dimissionTime;

    @ApiModelProperty("分配客户数")
    private Integer allocateCustomerNum;

    @ApiModelProperty("分配群聊数")
    private Integer allocateGroupNum;

    @ApiModelProperty("离职是否分配(1:已分配;0:未分配;)")
    private Integer isAllocate;
}
