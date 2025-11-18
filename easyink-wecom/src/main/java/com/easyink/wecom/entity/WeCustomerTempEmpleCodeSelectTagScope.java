package com.easyink.wecom.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户专属员工活码选择标签范围表(WeCustomerTempEmpleCodeSelectTagScope)表实体类
 *
 * @author tigger
 * @since 2025-04-29 14:53:34
 */
@TableName("we_customer_temp_emple_code_select_tag_scope")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeCustomerTempEmpleCodeSelectTagScope {

    @TableId(type = IdType.AUTO)
    private Long id;
    //授权企业ID
    private String corpId;
    //原员工活码id
    private Long originEmpleCodeId;
    //选择标签详情json
    private Integer type;
    private String value;
    //创建人(后续员工专属活码存储员工id)
    private String createBy;
    //创建时间
    private Date createTime;

}

