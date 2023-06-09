package com.easyink.wecom.domain.entity.autotag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 标签规则表(WeAutoTagRule)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:40
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagRule {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则类型 1:关键词 2:入群 3:新客
     */
    private Integer labelType;
    /**
     * 启用禁用状态 0:禁用1:启用
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;

}

