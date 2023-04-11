package com.easyink.wecom.domain.entity.form;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.easyink.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单表(WeForm)表实体类
 *
 * @author tigger
 * @since 2023-01-09 15:00:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WeForm extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 表单头图
     */
    private String headImageUrl;
    /**
     * 表单名称
     */
    private String formName;
    /**
     * 表单说明
     */
    private String description;
    /**
     * 提交按钮文本描述
     */
    private String submitText;
    /**
     * 提交按钮颜色
     */
    private String submitColor;
    /**
     * 表单字段列表json
     */
    private String formFieldListJson;
    /**
     * 表单分组id
     */
    private Integer groupId;
    /**
     * 启用标识(0: 未启用 1:启用)
     */
    private Boolean enableFlag;
    /**
     * 删除标识 0: 未删除 1:删除
     */
    private Integer delFlag;
    /**
     * 唯一键删除id(删除的时候给deleteId设置为主键id(不重复))
     */
    private Integer deleteId;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 头图开关(false:关闭 true:开启)
     */
    private Boolean headImageOpenFlag;
    /**
     * 显示排序开关(false:关闭 true:开启)
     */
    private Boolean showSortFlag;
    /**
     * 表单说明开关(false:关闭 true:开启)
     */
    private Boolean descriptionFlag;


}

