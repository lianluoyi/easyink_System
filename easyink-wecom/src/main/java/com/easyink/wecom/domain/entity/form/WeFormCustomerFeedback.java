package com.easyink.wecom.domain.entity.form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户好评评价表(WeFormCustomerFeedback)表实体类
 *
 * @author tigger
 * @since 2023-01-13 16:10:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeFormCustomerFeedback {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 表单id
     */
    private Long formId;
    /**
     * 客户id
     */
    private String customerId;
    /**
     * 员工id
     */
    private String userId;
    /**
     * 评分值(1-10)
     */
    private Integer score;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 创建时间(提交时间)
     */
    private Date createTime;


}

