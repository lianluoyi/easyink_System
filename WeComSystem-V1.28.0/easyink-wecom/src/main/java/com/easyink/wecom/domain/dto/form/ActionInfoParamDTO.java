package com.easyink.wecom.domain.dto.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交结果行为参数DTO
 *
 * @author tigger
 * 2023/1/11 16:12
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionInfoParamDTO {


    /**
     * 链接URL
     */
    private String url;

    /**
     * 图标url
     */
    private String iconUrl;

    /**
     * 文案
     */
    private String document;

}
