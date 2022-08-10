package com.easyink.wecom.domain.vo.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 类名: 分配离职员工返回给前端的参数实体
 *
 * @author : silver_chariot
 * @date : 2021/12/3 17:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResignedUserVO {

    @ApiModelProperty(value = "分配总记录id")
    private Long id;
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

    @ApiModelProperty("待分配客户数")
    private Integer allocateCustomerNum;

    @ApiModelProperty("待分配群聊数")
    private Integer allocateGroupNum;

    @ApiModelProperty("离职是否分配(1:已分配;0:未分配;)")
    private Integer isAllocate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接替时间")
    private Date transferTime;


}
