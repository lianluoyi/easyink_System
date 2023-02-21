package com.easyink.wecom.domain.entity.form;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 表单分组表(WeFormGroup)表实体类
 *
 * @author tigger
 * @since 2023-01-09 11:23:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("we_form_group")
public class WeFormGroup extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 父分组id
     */
    private Integer pId;
    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组所属类别(1:企业 2: 部门 3:个人)
     */
    private Integer sourceType;
    /**
     * 所属分组id， sourceType = 2时才有值，默认0
     */
    private Long departmentId;
    /**
     * 删除标识 0: 未删除 1:删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 唯一键删除id(删除的时候给deleteId设置为主键id(不重复))
     */
    private Integer deleteId;
    /**
     * 排序号
     */
    private Integer sort;

}

