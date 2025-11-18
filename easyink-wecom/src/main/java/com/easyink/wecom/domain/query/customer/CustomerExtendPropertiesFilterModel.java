package com.easyink.wecom.domain.query.customer;

import com.easyink.wecom.domain.WeCustomerRel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tigger
 * 2025/5/6 15:10
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerExtendPropertiesFilterModel {
    /**
     * 当列表为空, 是否判断为 查询拿不到数据, 默认true,
     * 位置信息不使用扩展字段进行过滤,  而是使用单独的字段进行过滤, 所以设置为false
     */
    private boolean needFilter = true;
    /**
     * 由扩展字段等非客户表自身字段 过滤出的员工客户id关系列表
     */
    private List<WeCustomerRel> list = new ArrayList<>();
}
