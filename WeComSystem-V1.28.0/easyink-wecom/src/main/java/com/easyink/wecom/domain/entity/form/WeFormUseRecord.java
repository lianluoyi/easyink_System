package com.easyink.wecom.domain.entity.form;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 表单使用记录表(WeFormUseRecord)表实体类
 *
 * @author tigger
 * @since 2023-01-13 10:12:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeFormUseRecord {

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
     * 员工id
     */
    private String userId;
    /**
     * 客户id
     */
    private String externalUserId;

    /**
     * 企业id
     */
    private String corpId;
    /**
     * 使用时间(相同唯一索引则更新使用时间为新的)
     */
    private Date useTime;

}

