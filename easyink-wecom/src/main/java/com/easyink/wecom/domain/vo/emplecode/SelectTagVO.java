package com.easyink.wecom.domain.vo.emplecode;

import com.easyink.wecom.domain.model.emplecode.TagGroupInfoModel;
import com.easyink.wecom.domain.model.emplecode.TagInfoModel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 标签可选范围VO
 *
 * @author tigger
 * 2025/4/29 13:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "专属活码设置可选标签DTO")
public class SelectTagVO {
    /**
     * 选择的标签分组详情
     */
    private List<TagGroupInfoModel> selectGroupList;

    /**
     * 选择的标签id列表
     */
    private List<TagInfoModel> selectTagList;
}