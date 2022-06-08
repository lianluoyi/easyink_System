package com.easywecom.wecom.domain.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： WeGroupTagDTO
 *
 * @author 佚名
 * @date 2021/9/8 17:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeGroupTagDTO {
    /**
     * 这是返回的判断参数，如果返回1：代表存在，0：代表不存在
     */
    private Integer repeat;
}
