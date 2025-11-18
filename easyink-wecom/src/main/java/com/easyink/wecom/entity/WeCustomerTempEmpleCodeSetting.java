package com.easyink.wecom.entity;


import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户临时员工活码表(WeCustomerTempEmpleCodeSetting)表实体类
 *
 * @author tigger
 * @since 2025-01-13 13:52:49
 */
@Builder
@TableName("we_customer_temp_emple_code_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeCustomerTempEmpleCodeSetting {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 授权企业ID
     */
    private String corpId;
    /**
     * 原员工活码id
     */
    private Long originEmpleCodeId;
    /**
     * 是否打备注开关
     */
    private Boolean remarkOpen;
    /**
     * 备注类型：0：不备注，1：在昵称前，2：在昵称后
     */
    private Integer remarkType;
    /**
     * 备注名
     */
    private String remarkName;
    /**
     * 标签id列表
     */
    private String tagIds;
    /**
     * 客户扩展信息
     */
    private String customerExtendInfo;
    /**
     * 好友回调自定义参数state
     */
    private String state;
    /**
     * 新增联系方式的配置id
     */
    private String configId;
    /**
     * 二维码链接
     */
    private String qrCode;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 创建人(后续员工专属活码存储员工id)
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

}

