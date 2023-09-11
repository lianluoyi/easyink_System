package com.easyink.wecom.domain.dto.emplecode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获客链接请求实体
 *
 * @author lichaoyu
 * @date 2023/8/22 10:28
 */
@Data
@ApiModel("获客链接请求实体")
public class CustomerAssistantDTO {

    @ApiModelProperty("链接id")
    private String link_id;
    @ApiModelProperty("链接名称")
    private String link_name;
    @ApiModelProperty("是否无需验证，默认为true")
    private Boolean skip_verify = Boolean.TRUE;
    @ApiModelProperty("获客链接所属员工/部门范围")
    private Range range;

    @Data
    public static class Range {
        @ApiModelProperty("获客链接关联的userid列表，最多可关联100个")
        private String[] user_list;
        @ApiModelProperty("获客链接关联的部门id列表，部门覆盖总人数最多100个")
        private Long[] department_list;
    }
}
