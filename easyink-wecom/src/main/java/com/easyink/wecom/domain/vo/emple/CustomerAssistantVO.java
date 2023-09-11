package com.easyink.wecom.domain.vo.emple;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 获客链接返回实体类
 *
 * @author lichaoyu
 * @date 2023/8/29 13:59
 */
@Data
public class CustomerAssistantVO {

    @ApiModelProperty(value = "获客链接主键Id")
    private Long id;

    @ApiModelProperty(value = "授权企业ID", hidden = true)
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "获客链接名称")
    private String scenario;

    @ApiModelProperty(value = "获客链接id")
    private String linkId;

    @ApiModelProperty(value = "获客链接url")
    private String qrCode;

    @ApiModelProperty("使用者")
    private String useUserName;

    @ApiModelProperty("使用部门")
    private String departmentName;

    @ApiModelProperty("创建人主部门")
    private String mainDepartmentName;

    @ApiModelProperty(value = "使用员工")
    private List<WeEmpleCodeUseScop> weEmpleCodeUseScops;
}
