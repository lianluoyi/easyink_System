package com.easyink.wecom.domain.model.emplecode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagGroupInfoModel {
    /**
     * 标签分组id
     */
    private String groupId;
    /**
     * 标签分组名称
     */
    private String groupName;
}