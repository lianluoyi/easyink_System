package com.easyink.wecom.domain.dto;

import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeCustomer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 类名： WeCustomerPushMessageDTO
 *
 * @author 佚名
 * @date 2021/11/15 12:30
 */

@ApiModel("群发客户查询对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeCustomerPushMessageDTO extends WeCustomer {

    /**
     * 包含标签模式 {@link com.easyink.wecom.domain.enums.TagFilterModeEnum}
     */
    private Integer includeTagMode;
    /**
     * 过滤的标签列表
     */
    private String filterTags;
    /**
     * 过滤标签模式 {@link com.easyink.wecom.domain.enums.TagFilterModeEnum}
     */
    private Integer filterTagMode;
    /**
     * 是否需要sql过滤标签, 默认true, 兼容原来的逻辑
     */
    private boolean needSqlFilterTag = true;
    @ApiModelProperty("过滤员工列表，用逗号隔开")
    private String filterUsers;
    @ApiModelProperty("过滤部门列表，用逗号隔开")
    private String filterDepartments;
    @ApiModelProperty("过滤员工添加的客户列表")
    private List<String> filterExternalList;
    @ApiModelProperty("开始时间")
    private Date customerStartTime;
    @ApiModelProperty("结束时间")
    private Date customerEndTime;
    @ApiModelProperty(value = "使用范围，全部客户0，指定客户1")
    private String pushRange;
    @ApiModelProperty(value = "来源")
    private String addWay;

    public WeCustomerPushMessageDTO(String users, String tags, String corpId, String departmentIds, String pushRange) {
        this.setUserIds(users);
        this.setTagIds(tags);
        this.setCorpId(corpId);
        this.setDepartmentIds(departmentIds);
        this.setPushRange(pushRange);
    }

    /**
     * 转换截止日期格式
     *
     * @return 格式为YYYY-MM-DD 23:59:59 的日期
     */
    public Date getCustomerEndTime() {
        return DateUtils.getEndOfDay(customerEndTime);
    }
}
