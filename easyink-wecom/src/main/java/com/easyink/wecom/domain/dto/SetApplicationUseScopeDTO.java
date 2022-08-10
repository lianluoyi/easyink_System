package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名: BaseApplicationDTO
 *
 * @author: 1*+
 * @date: 2021-10-18 9:32
 */
@Data
@ApiModel("设置我的应用使用范围实体")
public class SetApplicationUseScopeDTO extends BaseApplicationDTO {

    @ApiModelProperty("应用配置")
    @NotNull(message = "应用配置不能为空")
    private List<UseScope> useScopeList;


    @Data
    @ApiModel("使用范围")
    @NoArgsConstructor
    public static class UseScope {

        @ApiModelProperty("使用类型(1指定员工，2指定角色，3使用部门)")
        @Min(1L)
        @Max(3L)
        private Integer type;

        @NotBlank(message = "参数不能为空")
        @ApiModelProperty("指定员工存userId,指定角色存角色ID，指定部门存departmentId")
        private String val;

        @ApiModelProperty("当类型为员工时前端要求返回员工名,为部门时显示部门名")
        private String name;
    }
}
