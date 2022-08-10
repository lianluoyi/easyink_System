package com.easywecom.wecom.domain.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 标签删除参数实体
 * @author admin
 * @create: 2020-10-18 00:22
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeCropDelDTO {

    private String[] tag_id;

    private String[] group_id;

}
