package com.easyink.wecom.domain.vo.form;

import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 客户操作记录导出报表VO
 *
 * @author wx
 * 2023/1/30 10:33
 **/
@NoArgsConstructor
@Data
public class FormCustomerOperRecordExportVO extends FormOperRecordDetailVO{

    @ApiModelProperty("发送智能表单的员工id")
    private String userId;

    @ApiModelProperty("员工名称")
    @Excel(name = "所属员工", sort = 2)
    private String userName;

    @ApiModelProperty("员工所属部门")
    private String departmentName;

    @ApiModelProperty("创建时间/点击时间")
    @Excel(name = "点击时间", sort = 4, dateFormat ="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 扩展属性与值的映射, K:问题,V答案 {@link com.easyink.wecom.domain.model.form.FormResultModel}
     */
    private Map<String, String> extendPropMapper;


}
