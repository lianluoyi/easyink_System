package com.easyink.wecom.domain.vo;

import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeGroupCode;
import com.easyink.wecom.domain.WeGroupCodeActual;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：WeEmpleCodeVO
 *
 * @author Society my sister Li
 * @date 2021-11-02 16:54
 */
@Data
@ApiModel("员工活码返回实体")
public class WeEmpleCodeVO extends WeEmpleCode {

    @ApiModelProperty("使用者")
    private String useUserName;

    @ApiModelProperty("使用部门")
    private String departmentName;

    @ApiModelProperty("使用者手机号")
    private String mobile;

    @ApiModelProperty("是否开启自动通过：0不开启，1开启")
    private Integer isAutoPass;

    @ApiModelProperty("是否开启自动备注：0不设置，1设置")
    private Integer isAutoSetRemark;

    @ApiModelProperty("群活码信息")
    private WeGroupCode weGroupCode;

    @ApiModelProperty("实际群信息")
    private List<WeGroupCodeActual> groupList;

    @ApiModelProperty("通过该员工活码添加的人数统计")
    private Integer cusNumber;

    @ApiModelProperty("创建人主部门")
    private String mainDepartmentName;


}
