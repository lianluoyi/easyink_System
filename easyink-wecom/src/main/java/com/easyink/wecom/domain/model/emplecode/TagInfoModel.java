package com.easyink.wecom.domain.model.emplecode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagInfoModel {
    /**
     * 标签id
     */
    private String tagId;
    /**
     * 标签名称
     */
    private String tagName;
}