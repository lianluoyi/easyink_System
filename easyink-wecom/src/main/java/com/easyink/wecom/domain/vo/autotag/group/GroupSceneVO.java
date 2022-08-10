package com.easyink.wecom.domain.vo.autotag.group;

import com.easyink.wecom.domain.vo.autotag.GroupInfoVO;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author tigger
 * 2022/2/28 15:26
 **/
@Data
public class GroupSceneVO {

    @ApiModelProperty("场景id")
    private Long id;
    @ApiModelProperty("群名称列表")
    private List<GroupInfoVO> groupList;
    @ApiModelProperty("标签名称列表")
    private List<TagInfoVO> tagList;
}
