package com.easyink.web.controller.order;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.client.OrderClient;
import com.easyink.wecom.domain.OrderGroupToOrderCustomerEntity;
import com.easyink.wecom.domain.OrderUserToOrderAccountEntity;
import com.easyink.wecom.domain.dto.BindDetailDTO;
import com.easyink.wecom.domain.dto.UnBindOrderDTO;
import com.easyink.wecom.domain.dto.unBindCustomerDTO;
import com.easyink.wecom.domain.order.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.OrderGroupToOrderCustomerService;
import com.easyink.wecom.service.OrderUserToOrderAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * 类名: OrderManagerController
 *
 * @author: 1*+
 * @date: 2021-12-13 17:51
 */
@RestController
@RequestMapping("/wecom/order")
@Api(tags = "壹鸽工单应用接口")
@Slf4j
public class OrderManagerController extends BaseController {
    private final OrderClient orderClient;
    private final OrderUserToOrderAccountService orderUserToOrderAccountService;
    private final OrderGroupToOrderCustomerService groupToOrderCustomerService;

    @Autowired
    public OrderManagerController(OrderClient orderClient, OrderUserToOrderAccountService orderUserToOrderAccountService, OrderGroupToOrderCustomerService groupToOrderCustomerService) {
        this.orderClient = orderClient;
        this.orderUserToOrderAccountService = orderUserToOrderAccountService;
        this.groupToOrderCustomerService = groupToOrderCustomerService;
    }

    /**
     * 校验网点ID
     */
    @GetMapping("/verifyNetwork")
    @ApiOperation("校验网点ID")
    public AjaxResult<OrderVerifyVO> verifyNetwork(@Validated @NotBlank @RequestParam("networkId") String networkId) {
        return AjaxResult.success(orderUserToOrderAccountService.verifyNetworkId(networkId));
    }

    /**
     * 获取网点账号列表接口
     */
    @GetMapping("/networkUser")
    @ApiOperation("获取工单账号列表接口")
    public AjaxResult<OrderUser> networkUser() {
        return AjaxResult.success(orderUserToOrderAccountService.networkUser(LoginTokenService.getLoginUser().getCorpId()));
    }


    /**
     * 获取网点客户列表接口
     */
    @GetMapping("/networkCustomer")
    @ApiOperation("获取网点客户列表接口")
    public AjaxResult<OrderCustomer> networkCustomer() {
        return AjaxResult.success(orderUserToOrderAccountService.networkCustomer(LoginTokenService.getLoginUser().getCorpId()));
    }

    /**
     * 工单列表接口
     */
    @PostMapping("/listOrder")
    @ApiOperation("工单列表接口")
    public AjaxResult<OrderListVO> listOrder(@Validated @RequestBody OrderListDTO orderListDTO) {
        orderListDTO.setNetworkId(orderUserToOrderAccountService.getNetworkId(LoginTokenService.getLoginUser().getCorpId()));
        orderListDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(groupToOrderCustomerService.listOrder(orderListDTO));
    }

    /**
     * 状态变更接口
     */
    @PostMapping("/handleOrder")
    @ApiOperation("状态变更接口")
    public AjaxResult<OrderHandlerVO> handleOrder(@Validated @RequestBody OrderHadlerDTO orderHadlerDTO) {
        orderHadlerDTO.setNetworkId(orderUserToOrderAccountService.getNetworkId(LoginTokenService.getLoginUser().getCorpId()));
        return AjaxResult.success(orderClient.handleOrder(orderHadlerDTO).getResult());
    }

    /**
     * 工单详情接口
     */
    @PostMapping("/descOrder")
    @ApiOperation("工单详情接口")
    public AjaxResult<Order> descOrder(@Validated @RequestBody OrderDetailDTO orderDetailDTO) {
        orderDetailDTO.setNetworkId(orderUserToOrderAccountService.getNetworkId(LoginTokenService.getLoginUser().getCorpId()));
        return AjaxResult.success(orderClient.descOrder(orderDetailDTO).getResult());
    }

    /**
     * 工单数统计接口
     */
    @PostMapping("/totalNumOrder")
    @ApiOperation("工单数统计接口")
    public AjaxResult<OrderTotal> totalNumOrder(@Validated @RequestBody OrderTotalDTO orderTotalDTO) {
        orderTotalDTO.setNetworkId(orderUserToOrderAccountService.getNetworkId(LoginTokenService.getLoginUser().getCorpId()));
        return AjaxResult.success(orderClient.totalNumOrder(orderTotalDTO).getResult());
    }


    @PostMapping("/bindUser")
    @ApiOperation("绑定员工工单账号")
    public AjaxResult bindUser(@Validated @RequestBody OrderUserToOrderAccountEntity orderAccountEntity) {
        orderAccountEntity.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        orderUserToOrderAccountService.bindUser(orderAccountEntity);
        return AjaxResult.success();
    }

    @PostMapping("/unbindUser")
    @ApiOperation("解绑员工工单")
    public AjaxResult unbindUser(@Validated @RequestBody UnBindOrderDTO orderAccountEntity) {
        orderAccountEntity.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        orderUserToOrderAccountService.unbindUser(orderAccountEntity);
        return AjaxResult.success();
    }

    @PostMapping("/bindCustomer")
    @ApiOperation("绑定客户")
    public AjaxResult bindCustomer(@Validated @RequestBody OrderGroupToOrderCustomerEntity groupToOrderCustomerEntity) {
        groupToOrderCustomerEntity.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        groupToOrderCustomerService.bindCustomer(groupToOrderCustomerEntity);
        return AjaxResult.success();
    }

    @PostMapping("/unbindCustomer")
    @ApiOperation("解绑客户")
    public AjaxResult unbindCustomer(@Validated @RequestBody unBindCustomerDTO unBindCustomerDTO) {
        unBindCustomerDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        groupToOrderCustomerService.unbindCustomer(unBindCustomerDTO);
        return AjaxResult.success();
    }


    @GetMapping("/getBindTotal")
    @ApiOperation("获得员工数量绑定数量")
    public AjaxResult getBindTotal() {
        return AjaxResult.success(orderUserToOrderAccountService.getBindTotal(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/listOfBindDetail")
    @ApiOperation("获取绑定详情")
    public TableDataInfo listOfBindDetail(BindDetailDTO bindDetailDTO) {
        bindDetailDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        bindDetailDTO.setNetworkId(orderUserToOrderAccountService.getNetworkId(bindDetailDTO.getCorpId()));
        startPage();
        return getDataTable(orderUserToOrderAccountService.listOfBindDetail(bindDetailDTO));
    }

    @GetMapping("/getBindInfo")
    @ApiOperation("获取员工客户绑定信息")
    public AjaxResult<OrderBindInfoVO> getBindInfo(OrderBindInfoDTO orderBindInfoDTO) {
        if (!LoginTokenService.getLoginUser().isSuperAdmin()) {
            orderBindInfoDTO.setUserId(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        orderBindInfoDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(orderUserToOrderAccountService.getBindInfo(orderBindInfoDTO));
    }

    /**
     * 机器人推送工单创建接口
     */
    @PostMapping("/createOrder")
    @ApiOperation("创建工单")
    public AjaxResult<OrderCreate> createOrder(@Validated @RequestBody OrderCreateDTO orderCreateDTO) {
        return AjaxResult.success(orderClient.createOrder(orderCreateDTO));
    }
}
