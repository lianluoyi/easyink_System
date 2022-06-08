package com.easywecom.wecom.domain.dto.tag;

import cn.hutool.core.collection.CollectionUtil;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.WeTagGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 标签组
 * @author admin
 * @create: 2020-10-17 20:02
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeCropGroupTagDTO {
    private String group_id;
    private String group_name;
    private Integer order;
    private Boolean deleted;
    private Long create_time;
    private List<WeCropTagDTO> tag;

    /**
     * 获取新增的实体
     *
     * @param weTagGroup
     * @return
     */
    public static WeCropGroupTagDTO transformAddTag(WeTagGroup weTagGroup) {
        WeCropGroupTagDTO weCropGroupTagDTO = WeCropGroupTagDTO.builder()
                .group_id(weTagGroup.getGroupId())
                .group_name(weTagGroup.getGroupName())
                .build();
        List<WeTag> weTags = weTagGroup.getWeTags();
        if (CollectionUtil.isNotEmpty(weTags)) {
            //新增的标签
            List<WeTag> newAddWeTag
                    = weTags.stream().filter(v -> StringUtils.isEmpty(v.getTagId())).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(newAddWeTag)) {
                List<WeCropTagDTO> weTagDtos = new ArrayList<>();

                newAddWeTag.forEach(weTag -> {
                    weTagDtos.add(WeCropTagDTO.builder()
                            .id(weTag.getTagId())
                            .name(weTag.getName())
                            .build());
                });
                weCropGroupTagDTO.setTag(weTagDtos);
            }
        }
        return weCropGroupTagDTO;
    }
}
