package com.easywecom.web.controller.order;

import com.alibaba.fastjson.JSON;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.enums.AppIdEnum;
import com.easywecom.common.enums.ExpressNameEnum;
import com.easywecom.common.enums.LoginTypeEnum;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.wecom.domain.entity.appconfig.YiGeOrderAppConfig;
import com.easywecom.wecom.domain.order.OrderLoginVO;
import com.easywecom.wecom.domain.order.OrderNetworkVO;
import com.easywecom.wecom.domain.vo.MyApplicationIntroductionVO;
import com.easywecom.wecom.login.service.SysLoginService;
import com.easywecom.wecom.service.WeMyApplicationService;
import com.easywecom.wecom.service.WeMyApplicationUseScopeService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: OrderLoginController
 *
 * @author: 1*+
 * @date: 2021-12-14 10:55
 */
@RestController
@RequestMapping("/wecom/order")
@Api(tags = "壹鸽工单应用接口")
@Slf4j
public class OrderLoginController extends BaseController {


    private final SysLoginService loginService;
    private final WeMyApplicationUseScopeService weMyApplicationUseScopeService;
    private final WeMyApplicationService weMyApplicationService;

    @Autowired
    public OrderLoginController(SysLoginService loginService, WeMyApplicationUseScopeService weMyApplicationUseScopeService, WeMyApplicationService weMyApplicationService) {
        this.loginService = loginService;
        this.weMyApplicationUseScopeService = weMyApplicationUseScopeService;
        this.weMyApplicationService = weMyApplicationService;
    }


    @ApiOperation("AI系统企业三方扫码登录")
    @ApiResponses({
            @ApiResponse(code = 1001, message = "所在企业未授权当前应用，请联系企业管理员进行授权"),
            @ApiResponse(code = 7000, message = "所在企业未开通「壹鸽快递工单助手」，请联系管理员"),
            @ApiResponse(code = 7001, message = "您没有访问权限，请联系管理员"),
            @ApiResponse(code = 7002, message = "所在企业未开通「壹鸽快递工单助手」，请联系管理员")
    })
    @GetMapping("/login")
    public AjaxResult<OrderLoginVO> qrCodeLogin3rd(@ApiParam("扫码登录返回的授权码") @RequestParam("authCode") String authCode) {
        //登录解析LoginUser
        LoginResult loginResult = loginService.loginHandler(authCode, LoginTypeEnum.BY_THIRD_SCAN.getState());
        if (StringUtils.isNotBlank(loginResult.getErrorMsg())) {
            return AjaxResult.error(loginResult.getErrorMsg());
        }

        LoginUser loginUser = loginResult.getLoginUser();
        if (loginUser == null || loginUser.getWeUser() == null) {
            return AjaxResult.error(ResultTip.TIP_MISSING_LOGIN_INFO);
        }
        //判断当前LoginUser是否在可登陆AI系统范围内
        List<String> userIdList = weMyApplicationUseScopeService.getUseScopeUserList(loginUser.getCorpId(), AppIdEnum.YIGE_ORDER.getCode());
        if (CollectionUtils.isEmpty(userIdList) || !userIdList.contains(loginUser.getWeUser().getUserId())) {
            return AjaxResult.error(ResultTip.TIP_UN_USE_AI_SYSTEM);
        }
        OrderLoginVO orderLoginVO = new OrderLoginVO(loginUser);

        //获取网点配置
        MyApplicationIntroductionVO myApplicationIntroductionVO = weMyApplicationService.getMyApplicationDetail(loginUser.getCorpId(), AppIdEnum.YIGE_ORDER.getCode());
        if (StringUtils.isNotBlank(myApplicationIntroductionVO.getConfig())) {
            YiGeOrderAppConfig yiGeOrderAppConfig = JSON.parseObject(myApplicationIntroductionVO.getConfig(), YiGeOrderAppConfig.class);
            //如果网点id为空，直接返回未绑定网点
            if (StringUtils.isBlank(yiGeOrderAppConfig.getNetworkId())) {
                return AjaxResult.error(ResultTip.TIP_UN_BIND_NETWORK);
            }
            orderLoginVO.setNetworkId(yiGeOrderAppConfig.getNetworkId());
            orderLoginVO.setNetworkName(yiGeOrderAppConfig.getNetworkName());
            orderLoginVO.setExpressName(ExpressNameEnum.getEnum(yiGeOrderAppConfig.getType()).getDesc());
        } else {
            //配置为空也直接返回未绑定网点
            return AjaxResult.error(ResultTip.TIP_UN_BIND_NETWORK);
        }
        return AjaxResult.success(orderLoginVO);
    }


    @ApiOperation("AI系统拉取已绑定的网点列表")
    @ApiResponses({
    })
    @GetMapping("/getNetworkList")
    public AjaxResult<OrderNetworkVO> getNetworkList() {
        List<OrderNetworkVO> result = new ArrayList<>();

        List<MyApplicationIntroductionVO> list = weMyApplicationService.listOfMyApplication(AppIdEnum.YIGE_ORDER.getCode());
        if (CollectionUtils.isEmpty(list)) {
            return AjaxResult.success(result);
        }

        for (MyApplicationIntroductionVO myApplicationIntroductionVO : list) {
            if (StringUtils.isNotBlank(myApplicationIntroductionVO.getConfig())) {
                YiGeOrderAppConfig yiGeOrderAppConfig = JSON.parseObject(myApplicationIntroductionVO.getConfig(), YiGeOrderAppConfig.class);
                if (yiGeOrderAppConfig == null) {
                    continue;
                }
                OrderNetworkVO orderNetworkVO = new OrderNetworkVO();
                orderNetworkVO.setExpressName(ExpressNameEnum.getEnum(yiGeOrderAppConfig.getType()).getDesc());
                orderNetworkVO.setNetworkId(yiGeOrderAppConfig.getNetworkId());
                orderNetworkVO.setNetworkName(yiGeOrderAppConfig.getNetworkName());
                orderNetworkVO.setCompanyName(myApplicationIntroductionVO.getCompanyName());
                orderNetworkVO.setCorpId(myApplicationIntroductionVO.getCorpId());
                result.add(orderNetworkVO);
            }
        }
        return AjaxResult.success(result);
    }


}
