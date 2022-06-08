package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.OrderGroupToOrderCustomerEntity;
import com.easywecom.wecom.domain.dto.unBindCustomerDTO;
import com.easywecom.wecom.domain.order.OrderListDTO;
import com.easywecom.wecom.domain.order.OrderListMainVO;

/**
 * 类名： 企业客户群与工单客户绑定关系接口
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
public interface OrderGroupToOrderCustomerService extends IService<OrderGroupToOrderCustomerEntity> {
    /**
     * 绑定群和对应客户
     *
     * @param groupToOrderCustomerEntity 实体
     */
    void bindCustomer(OrderGroupToOrderCustomerEntity groupToOrderCustomerEntity);

    /**
     * 解绑群和对应客户
     *
     * @param unBindCustomerDTO
     */
    void unbindCustomer(unBindCustomerDTO unBindCustomerDTO);

    /**
     * 工单列表
     *
     * @param orderListDTO
     * @return
     */
    OrderListMainVO listOrder(OrderListDTO orderListDTO);
}

