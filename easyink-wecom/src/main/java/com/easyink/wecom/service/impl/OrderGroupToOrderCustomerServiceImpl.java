package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.OrderClient;
import com.easyink.wecom.domain.OrderGroupToOrderCustomerEntity;
import com.easyink.wecom.domain.dto.unBindCustomerDTO;
import com.easyink.wecom.domain.order.OrderListDTO;
import com.easyink.wecom.domain.order.OrderListMainVO;
import com.easyink.wecom.mapper.OrderGroupToOrderCustomerMapper;
import com.easyink.wecom.service.OrderGroupToOrderCustomerService;
import com.easyink.wecom.service.OrderUserToOrderAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 类名： OrderGroupToOrderCustomerServiceImpl
 *
 * @author 佚名
 * @date 2021/12/15 11:53
 */
@Service
@Slf4j
public class OrderGroupToOrderCustomerServiceImpl extends ServiceImpl<OrderGroupToOrderCustomerMapper, OrderGroupToOrderCustomerEntity> implements OrderGroupToOrderCustomerService {
    private final OrderClient orderClient;

    public OrderGroupToOrderCustomerServiceImpl(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    public void bindCustomer(OrderGroupToOrderCustomerEntity groupToOrderCustomerEntity) {
        StringUtils.checkCorpId(groupToOrderCustomerEntity.getCorpId());
        //校验群是否绑定别的客户
        OrderGroupToOrderCustomerEntity customerEntity = this.getOne(new LambdaQueryWrapper<OrderGroupToOrderCustomerEntity>()
                .eq(OrderGroupToOrderCustomerEntity::getChatId, groupToOrderCustomerEntity.getChatId())
                .eq(OrderGroupToOrderCustomerEntity::getCorpId, groupToOrderCustomerEntity.getCorpId()).last(GenConstants.LIMIT_1));
        if (customerEntity != null) {
            throw new CustomException(ResultTip.TIP_YIGE_CHAT_BIND_ERROR);
        }
        OrderUserToOrderAccountService userToOrderAccountService = SpringUtils.getBean(OrderUserToOrderAccountService.class);
        groupToOrderCustomerEntity.setNetworkId(userToOrderAccountService.getNetworkId(groupToOrderCustomerEntity.getCorpId()));
        this.save(groupToOrderCustomerEntity);
    }

    @Override
    public void unbindCustomer(unBindCustomerDTO unBindCustomerDTO) {
        this.remove(new LambdaQueryWrapper<OrderGroupToOrderCustomerEntity>()
                .eq(OrderGroupToOrderCustomerEntity::getChatId, unBindCustomerDTO.getChatId())
                .eq(OrderGroupToOrderCustomerEntity::getCorpId, unBindCustomerDTO.getCorpId()));
    }

    @Override
    public OrderListMainVO listOrder(OrderListDTO orderListDTO) {
        OrderGroupToOrderCustomerEntity groupToOrderCustomerEntity = this.getOne(new LambdaQueryWrapper<OrderGroupToOrderCustomerEntity>()
                .eq(OrderGroupToOrderCustomerEntity::getChatId, orderListDTO.getChatId())
                .eq(OrderGroupToOrderCustomerEntity::getCorpId, orderListDTO.getCorpId())
                .eq(OrderGroupToOrderCustomerEntity::getNetworkId, orderListDTO.getNetworkId()).last(GenConstants.LIMIT_1));
        OrderListMainVO orderListMainVO = new OrderListMainVO();
        //未绑定客户
        if (groupToOrderCustomerEntity == null) {
            throw new CustomException(ResultTip.TIP_YIGE_CHAT_NOT_BIND_ERROR);
        }
        orderListMainVO.setOrderCustomerName(groupToOrderCustomerEntity.getOrderCustomerName());
        orderListMainVO.setOrderCustomerId(groupToOrderCustomerEntity.getOrderCustomerId());
        orderListDTO.setCustomerId(groupToOrderCustomerEntity.getOrderCustomerId());
        try {
            orderListMainVO.setOrderList(orderClient.listOrder(orderListDTO).getResult());
        } catch (Exception e) {
            log.error("查询工单列表异常：ex:{}", ExceptionUtils.getStackTrace(e));
            orderListMainVO.setOrderList(new ArrayList<>());
        }
        return orderListMainVO;
    }
}