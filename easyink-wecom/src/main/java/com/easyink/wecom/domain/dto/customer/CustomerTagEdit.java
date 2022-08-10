package com.easyink.wecom.domain.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 客户标签编辑
 * @author admin
 * @create: 2020-10-24 21:01
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTagEdit {
    /**
     * 添加外部联系人的userid
     */
    private String userid;
    /**
     * 外部联系人userid
     */
    private String external_userid;
    /**
     * 要标记的标签列表
     */
    private String[] add_tag;
    /**
     * 要移除的标签列表
     */
    private String[] remove_tag;
}
