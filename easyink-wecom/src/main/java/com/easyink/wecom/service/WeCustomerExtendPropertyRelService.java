package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.sop.CustomerSopPropertyRel;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.dto.customersop.Column;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;

import java.util.List;

/**
 * 类名: 客户扩展属性关系表业务接口
 *
 * @author : silver_chariot
 * @date : 2021/11/15 18:14
 */
public interface WeCustomerExtendPropertyRelService extends IService<WeCustomerExtendPropertyRel> {
    /**
     * 修改客户-自定义字段关系
     *
     * @param weCustomer {@link WeCustomer}
     */
    void updateBatch(WeCustomer weCustomer);

    /**
     * 查询客户关系
     *
     * @param columnList 字段属性值
     * @return {@link List<String>}
     */
    List<String> listOfPropertyIdAndValue(List<Column> columnList);

    /**
     * 根据extend_property_id查询所有符合条件的客户额外字段关系
     *
     * @param columnList 字段属性值
     * @return {@link BaseExtendPropertyRel}
     */
    List<CustomerSopPropertyRel> selectBaseExtendValue(List<Column> columnList);
}
