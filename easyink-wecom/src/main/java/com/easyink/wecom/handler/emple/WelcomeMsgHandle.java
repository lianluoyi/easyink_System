package com.easyink.wecom.handler.emple;

import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.domain.dto.WeWelcomeMsg;
import com.easyink.wecom.domain.dto.customer.GetExternalDetailResp;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.domain.model.emplecode.State;
import com.easyink.wecom.domain.vo.welcomemsg.WeEmployMaterialVO;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.WeEmpleCodeService;
import com.easyink.wecom.service.WeMsgTlpMaterialService;
import com.easyink.wecom.service.WeMsgTlpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 欢迎语处理器
 *
 * @author tigger
 * 2025/8/26 14:54
 **/
@Service
@Slf4j
@AllArgsConstructor
public class WelcomeMsgHandle {

    private final WeEmpleCodeService weEmpleCodeService;
    private final WeCustomerClient weCustomerClient;
    private final WeMsgTlpService weMsgTlpService;
    private final WeMsgTlpMaterialService weMsgTlpMaterialService;
    private final WeCustomerService weCustomerService;

    /**
     * 发送欢迎语处理
     * @param state 添加好友成功state
     * @param welcomeCode 欢迎语code
     * @param customerId
     */
    public void sendWelcomeMsg(State state, String welcomeCode, CustomerId customerId) {

        // 非活码欢迎语
        if(StringUtils.isNotBlank(welcomeCode) && state != null  && StringUtils.isNotBlank(state.getState()) && !state.isFission()){
            // 活码欢迎语处理
            if (state.isAssistantState()) {
                // 获客链接欢迎语处理
                weEmpleCodeService.sendCustomerAssistantWelcomeMsg(state, welcomeCode, customerId);
            } else if (state.isCustomerEmploy()) {
                // 客户专属活码处理
                weEmpleCodeService.sendCustomerTempEmpleCodeWelcomeMsg(state, welcomeCode, customerId);
            } else {
                // 活码欢迎语处理
                weEmpleCodeService.sendUserEmpleCodeWelcomeMsg(state, welcomeCode, customerId, null);
            }
        } else if (StringUtils.isNotBlank(welcomeCode)) {
            otherHandle(welcomeCode, customerId);
        }
    }

    /**
     * 其他欢迎语处理
     *
     * @param welcomeCode    欢迎语code
     * @param customerId       客户id
     */
    private void otherHandle(String welcomeCode, CustomerId customerId) {
        log.info("[非活码欢迎语] welcomeCode:{}, userId:{}, externalUserId:{}, corpId:{}", welcomeCode, customerId.getUserId(), customerId.getExternalUserid(), customerId.getCorpId());
        WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder = WeWelcomeMsg.builder().welcome_code(welcomeCode);
        // 查询外部联系人与通讯录关系数据
        GetExternalDetailResp customerInfo = getCustomerInfo(customerId.getCorpId(), customerId.getExternalUserid());
        if (customerInfo == null || customerInfo.getExternalContact().getName() == null || customerInfo.getExternalContact().getGender() == null || CollectionUtils.isEmpty(customerInfo.getFollow_user())) {
            log.info("[非活码欢迎语] 缺少客户信息，不发送, customerInfo: {}", customerInfo);
            return;
        }

        // 客户名称
        String customerName = customerInfo.getExternalContact().getName();
        // 客户性别
        Integer gender = customerInfo.getExternalContact().getGender();
        // 客户来源，寻找客户详情信息中与添加员工对应的关系信息，员工-客户关系是唯一的，所以直接取列表第一个
        String addWay = customerInfo.getFollow_user().stream()
                .filter(item -> item.getUserId().equals(customerId.getUserId()))
                .collect(Collectors.toList())
                .get(0).getAdd_way();
        CompletableFuture.runAsync(() -> {
            try {
                WeEmployMaterialVO weEmployMaterialVO = weMsgTlpService.selectMaterialByUserId(customerId.getUserId(), customerId.getCorpId(), addWay, gender);
                // 当前员工存在命中的附件
                if (weEmployMaterialVO != null
                        && (CollectionUtils.isNotEmpty(weEmployMaterialVO.getWeMsgTlpMaterialList()) || StringUtils.isNotEmpty(weEmployMaterialVO.getDefaultMsg()))) {
                    // 构建欢迎语并发送
                    buildAndSendOtherWelcomeMsg(weEmployMaterialVO, customerId, weWelcomeMsgBuilder, customerName);
                    log.info("[非活码欢迎语] 好友欢迎语发送完成");
                }
            } catch (Exception e) {
                log.error("[非活码欢迎语] 发送欢迎语消息异常：ex:{}", ExceptionUtils.getStackTrace(e));
            }
        });
    }

    /**
     * 获取客户名称
     *
     * @param corpId         企业Id
     * @param externalUserid 客户id
     * @return 客户名称
     */
    public GetExternalDetailResp getCustomerInfo(String corpId, String externalUserid) {
        if (StringUtils.isAnyBlank(corpId, externalUserid)) {
            return null;
        }
        return weCustomerClient.getV2(externalUserid, corpId);
    }


    /**
     * 构建素材并发送
     */
    private void buildAndSendOtherWelcomeMsg(WeEmployMaterialVO weEmployMaterialVO, CustomerId customerId, WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder, String remark) {
        // 组装数据
        WeWelcomeMsg weWelcomeMsg = weMsgTlpMaterialService.buildWeWelcomeMsg(weEmployMaterialVO.getDefaultMsg(), weEmployMaterialVO.getWeMsgTlpMaterialList(), weWelcomeMsgBuilder, customerId, remark);
        // 调用企业微信接口发送欢迎语消息
        weCustomerService.sendWelcomeMsg(weWelcomeMsg, customerId.getCorpId());
    }
}
