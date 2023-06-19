package com.easyink.wecom.domain.dto;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class WeCustomerSearchDTO extends BaseEntity {

    @ApiModelProperty(value = "公司id")
    private String corpId;


    @ApiModelProperty(value = "外部联系人的userid")
    @NotBlank(message = "外部联系人的id不可为空")
    private String externalUserid;

    @ApiModelProperty(value = "外部联系人名称")
    private String name;

    /**
     * 备注
     */
    private String remark;

    @JSONField(defaultValue = "0")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属员工")
    private String userName;

    @ApiModelProperty(value = "员工id")
    private String userId;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "添加人id")
    @TableField(exist = false)
    private String userIds;

    @ApiModelProperty(value = "标签")
    private String tagIds;

    @ApiModelProperty(value = "来源")
    private String addWay;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "客户企业全称")
    //@TableField("corp_full_name")
    private String corpFullName;

    @ApiModelProperty(value = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String birthday;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "邮件")
    private String email;

    @ApiModelProperty(value = "客户地址")
    private String address;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "客户自定义字段")
    private List<BaseExtendPropertyRel> extendProperties = new ArrayList<>();

    /**
     * 筛选结果
     */
    @TableField(exist = false)
    private List<Long> extendList;

    @ApiModelProperty(value = "页数")
    private Integer pageNum;

    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;




}
