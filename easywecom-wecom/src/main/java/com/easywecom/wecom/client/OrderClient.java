package com.easywecom.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.easywecom.wecom.domain.order.*;
import com.easywecom.wecom.interceptor.OrderInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 类名: OrderClient
 *
 * @author: 1*+
 * @date: 2021-12-13 18:53
 */
@Component
@BaseRequest(baseURL = "${orderServerUrl}", interceptor = OrderInterceptor.class)
public interface OrderClient {
    /**
     * 校验网点ID
     */
    @Post(url = "/yg/OrderApi/verifyNetwork", dataType = "json")
    OrderBaseResp<OrderVerifyVO> verifyNetwork(@Body("networkId") String networkId);

    /**
     * 获取网点账号列表接口
     */
    @Post(url = "/yg/OrderApi/networkUser")
    OrderBaseResp<List<OrderUser>> networkUser(@Body("networkId") String networkId);

    /**
     * 获取网点客户列表接口
     */
    @Post(url = "/yg/OrderApi/networkCustomer")
    OrderBaseResp<List<OrderCustomer>> networkCustomer(@Body("networkId") String networkId);

    /**
     * 机器人推送工单创建接口
     */
    @Post(url = "/yg/OrderApi/createOrder")
    OrderBaseResp<OrderCreate> createOrder(@Validated @Body OrderCreateDTO orderCreateDTO);

    /**
     * 状态变更接口
     */
    @Post(url = "/yg/OrderApi/handleOrder")
    OrderBaseResp<OrderHandlerVO> handleOrder(@Body OrderHadlerDTO orderHadlerDTO);

    /**
     * 工单列表接口
     */
    @Post(url = "/yg/OrderApi/listOrder")
    OrderBaseResp<List<OrderListVO>> listOrder(@Validated @Body OrderListDTO orderListDTO);

    /**
     * 工单详情接口
     */
    @Post(url = "/yg/OrderApi/descOrder")
    OrderBaseResp<Order> descOrder(@Validated @Body OrderDetailDTO orderDetailDTO);

    /**
     * 工单数统计接口
     */
    @Post(url = "/yg/OrderApi/totalNumOrder")
    OrderBaseResp<OrderTotal> totalNumOrder(@Validated @Body OrderTotalDTO orderTotalDTO);


}
