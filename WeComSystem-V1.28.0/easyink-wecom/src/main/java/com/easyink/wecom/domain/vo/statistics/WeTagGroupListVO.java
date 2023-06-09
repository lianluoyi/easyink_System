package com.easyink.wecom.domain.vo.statistics;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签组信息VO
 *
 * @author lichaoyu
 * @date 2023/5/9 14:34
 */
@Data
@NoArgsConstructor
public class WeTagGroupListVO {

    /**
     * 标签组ID
     */
    private String tagGroupId;

    /**
     * 标签组名称
     */
    private String groupTagName;
}
