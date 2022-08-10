package com.easyink.wecom.domain.vo.sop;

import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.vo.WeOperationsCenterSopVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 类名： SopDetailVO
 *
 * @author 佚名
 * @date 2021/12/2 21:12
 */
@ApiModel("sop详情VO")
@Data
@NoArgsConstructor
public class SopDetailVO {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "企业ID", hidden = true)
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "SOP名称")
    private String name;

    @ApiModelProperty(value = "创建人.员工userId")
    private String createBy;

    @ApiModelProperty(value = "当为客户sop时 为使用客户")
    private List<CustomerSopVO> customerSopVOList;

    @ApiModelProperty(value = "使用群的群基本信息")
    private List<GroupSopVO> groupSopList;

    @ApiModelProperty(value = "使用对象")
    private List<SopUserVO> userList;

    @ApiModelProperty(value = "标签ID（多个逗号隔开 ）")
    private String tagId;

    @ApiModelProperty(value = "规则")
    private List<SopRuleVO> ruleList;

    @ApiModelProperty(value = "创建人userName")
    private String createUserName;

    @ApiModelProperty("主部门名称")
    private String mainDepartmentName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("过滤标签")
    private FindGroupSopFilterVO sopFilter;

    @ApiModelProperty("客户过滤条件")
    private GetCustomerSopFilterVO sopCustomerFilter;

    @ApiModelProperty(value = "使用群聊类型 0：指定群聊 ,1：筛选群聊")
    private Integer filterType;

    @ApiModelProperty("使用部门")
    private List<DepartmentVO> departmentList;

    public SopDetailVO(WeOperationsCenterSopVo sopEntity) {
        BeanUtils.copyProperties(sopEntity, this);
    }
}
