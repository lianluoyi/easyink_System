package com.easyink.wecom.domain.dto.autotag.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户场景DTO
 *
 * @author tigger
 * 2022/2/28 13:48
 **/
@Data
public class CustomerSceneDTO {
    @ApiModelProperty("主键id,修改传")
    private Long id;
    @ApiModelProperty("场景类型 1:天 2:周 3:月")
    private Integer sceneType;
    @ApiModelProperty("指定循环节点 周: 1-7 月: 1-月末")
    private Integer loopPoint;
    @ApiModelProperty("循环指定开始时间")
    private String loopBeginTime;
    @ApiModelProperty("循环指定结束时间")
    private String loopEndTime;


    @ApiModelProperty("标签id列表")
    private List<String> tagIdList;
    @ApiModelProperty("要删除的标签id列表")
    private List<String> removeTagIdList;
}
