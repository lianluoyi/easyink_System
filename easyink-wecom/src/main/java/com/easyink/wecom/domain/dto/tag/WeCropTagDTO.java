package com.easyink.wecom.domain.dto.tag;

import cn.hutool.core.collection.CollectionUtil;
import com.easyink.wecom.domain.WeTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 企业标签
 * @author admin
 * @create: 2020-10-17 20:03
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeCropTagDTO {
    private String id;
    private String name;
    private String order;
    private Boolean deleted;
    private Long create_time;

    public static List<WeCropTagDTO> transFormto(List<WeTag> weTags) {

        List<WeCropTagDTO> weCropDelDtoList = new ArrayList();
        if (CollectionUtil.isNotEmpty(weTags)) {

            weTags.forEach(k -> {
                weCropDelDtoList.add(
                        WeCropTagDTO.builder()
                                .name(k.getName())
                                .id(k.getTagId())
                                .build()
                );

            });

        }
        return weCropDelDtoList;

    }
}
