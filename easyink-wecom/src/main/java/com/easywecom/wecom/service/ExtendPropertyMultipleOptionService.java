package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import com.easywecom.wecom.domain.entity.customer.WeCustomerExtendProperty;

import java.util.List;
import java.util.Map;

/**
 * 类名: 客户扩展属性多选值业务接口
 *
 * @author : silver_chariot
 * @date : 2021/11/12 13:40
 */
public interface ExtendPropertyMultipleOptionService extends IService<ExtendPropertyMultipleOption> {
    /**
     * 编辑
     *
     * @param list {@link List<ExtendPropertyMultipleOption>}
     */
    void edit(List<ExtendPropertyMultipleOption> list);

    /**
     * 编辑
     *
     * @param property {@link WeCustomerExtendProperty}
     */
    void edit(WeCustomerExtendProperty property);

    /**
     * 根据扩展字段集合,获取多选值id到多选值详情的映射
     *
     * @param extendPropList {@link List<WeCustomerExtendProperty>}
     * @return 多选值id到多选值详情的映射
     */
    Map<Long, ExtendPropertyMultipleOption> getMapByProp(List<WeCustomerExtendProperty> extendPropList);

    /**
     * 根据扩展字段id集合,获取多选值id到多选值的映射
     *
     * @param extendPropIdList
     * @return
     */
    Map<Long, ExtendPropertyMultipleOption> getMapByPropId(List<Long> extendPropIdList);
}
