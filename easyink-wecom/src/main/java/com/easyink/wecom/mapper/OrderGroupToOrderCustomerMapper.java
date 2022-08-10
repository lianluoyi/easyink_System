package com.easyink.wecom.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.OrderGroupToOrderCustomerEntity;
import org.springframework.stereotype.Repository;

/**
 * 企业客户群与工单客户绑定关系
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
@Repository
public interface OrderGroupToOrderCustomerMapper extends BaseMapper<OrderGroupToOrderCustomerEntity> {

}
