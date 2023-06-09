package com.easyink.wecom.domain.vo.statistics;

import com.easyink.common.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据统计-标签统计-表格视图基础类
 *
 * @author lichaoyu
 * @date 2023/5/9 11:52
 */
@Data
@NoArgsConstructor
public class WeTagStatisticsBaseVO {

    /**
     * 标签ID
     */
    private String tagId;

    /**
     * 标签组ID
     */
    private String tagGroupId;

    /**
     * 标签名称
     */
    @Excel(name = "标签", sort = 1)
    private String tagName;

    /**
     * 标签组名称
     */
    @Excel(name = "所属标签组", sort = 2)
    private String groupTagName;


    /**
     * 标签创建时间
     */
    @Excel(name = "标签创建时间", sort = 4)
    private String createTime;

}
