package com.easyink.wecom.domain.dto.autotag.group;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 入群标签规则场景DTO
 *
 * @author tigger
 * 2022/2/28 9:35
 **/
@Data
public class GroupSceneDTO {
    @ApiModelProperty("场景id,修改时候传")
    private Long id;
    @ApiModelProperty("群id列表")
    private List<String> groupIdList;
    @ApiModelProperty("标签id列表")
    private List<String> tagIdList;

    @ApiModelProperty("要删除的群id列表,修改的时候传")
    private List<String> removeGroupIdList;
    @ApiModelProperty("要删除的标签id列表,修改传")
    private List<String> removeTagIdList;

}
