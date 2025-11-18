package com.easyink.wecom.factory.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.CustomerTrajectoryEnums;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.entity.WeExternalUseridMapping;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.domain.model.emplecode.State;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.handler.emple.WelcomeMsgHandle;
import com.easyink.wecom.openapi.dao.LockSelfBuildConfigMapper;
import com.easyink.wecom.openapi.domain.entity.LockSelfBuildConfig;
import com.easyink.wecom.openapi.service.LockSelfBuildApiService;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.autotag.WeAutoTagRuleHitCustomerRecordService;
import com.easyink.wecom.service.idmapping.WeExternalUserIdMappingService;
import com.easyink.wecom.utils.redis.CustomerRedisCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author admin
 * @description 新增客户事件
 * @date 2021/1/20 23:18
 **/
@AllArgsConstructor
@Slf4j
@Component("add_external_contact")
public class WeCallBackAddExternalContactImpl extends WeEventStrategy {
    private final WeCustomerService weCustomerService;
    private final WeEmpleCodeService weEmpleCodeService;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final RedisCache redisCache;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;
    private final WeAutoTagRuleHitCustomerRecordService weAutoTagRuleHitCustomerRecordService;
    private final CustomerRedisCache customerRedisCache;
    private final CustomerAssistantService customerAssistantService;
    private final LockSelfBuildApiService selfBuildApiService;
    private final LockSelfBuildConfigMapper lockSelfBuildConfigMapper;
    private final WeExternalUserIdMappingService weExternalUserIdMappingService;
    private final WelcomeMsgHandle welcomeMsgHandle;



    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (!checkParams(message)) {
            return;
        }
        CustomerId customerId = CustomerId.valueOf(message.getUserId(), message.getExternalUserId(), message.getToUserName());
        // 收到新增客户回调后,先进行欢迎语处理
        try {

            if(StringUtils.isBlank(message.getState()) && StringUtils.isBlank(message.getWelcomeCode())){
                log.error("[{}]:回调处理, 欢迎语参数异常, message:{}", message.getChangeType(), message);
                return;
            }

            State stateObj = State.valueOf(message.getState());
            welcomeMsgHandle.sendWelcomeMsg(stateObj, message.getWelcomeCode(), customerId);
        } catch (Exception e) {
            log.error("[{}]:回调处理,发送欢迎语未知异常, e : {}, message:{}", message.getChangeType(), ExceptionUtils.getStackTrace(e), message);
        }
        //  存入redis 后续由定时任务与编辑客户回调一起处理,避免同时处理导致的锁表 Tower 任务: 好友活码打标签失败 ( https://tower.im/teams/636204/todos/70202 )
        customerRedisCache.saveCallback(message.getToUserName(), message.getUserId(), message.getExternalUserId(), message);
    }

    /**
     * 添加客户回调处理
     *
     * @param message 回调消息体
     */
    public void addHandle(WxCpXmlMessageVO message) {
        String corpId = message.getToUserName();
        // 添加客户回调处理
        CustomerId customerId = CustomerId.valueOf(message.getUserId(), message.getExternalUserId(), message.getToUserName());
        try {
            // 先查询数据库中是否已经存在此客户
            // 若存在，则直接更新状态，
            if (existCustomer(customerId)) {
                weFlowerCustomerRelService.updateLossExternalUser(corpId, message.getUserId(), message.getExternalUserId());
            } else {
                // 不存在, 从远端获取信息
                weCustomerService.updateExternalContactV2(corpId, message.getUserId(), message.getExternalUserId());
            }
            // 处理明文密文映射
            handleDecrExternalUserId(message.getExternalUserId(), corpId);
        } catch (Exception e) {
            log.error("[{}]:回调处理,更新客户信息异常,message:{},e:{}", message.getChangeType(), message, ExceptionUtils.getStackTrace(e));
        }
        try {
            // 新客自动打标签处理
            weAutoTagRuleHitCustomerRecordService.makeTagToNewCustomer(message.getExternalUserId(), message.getUserId(), corpId);
        } catch (Exception e) {
            log.error("[{}]:回调处理,新课自动打标签异常,message:{},e:{}", message.getChangeType(), message, ExceptionUtils.getStackTrace(e));
        }
        // 客户轨迹记录 : 添加员工
        weCustomerTrajectoryService.saveActivityRecord(corpId, message.getUserId(), message.getExternalUserId(), CustomerTrajectoryEnums.SubType.ADD_USER.getType());

        // state存在，且不是任务裂变活码前缀，也不是获客链接前缀
        if(StringUtils.isBlank(message.getState())){
            return;
        }

        State state = State.valueOf(message.getState());

        if(state.isFission()){
            return;
        }
        if (state.isAssistantState()) {
            // 获客链接处理
            customerAssistantService.callBackAddAssistantHandle(message.getState(), message.getUserId(), message.getExternalUserId(), corpId);
            return;
        }
        // 活码处理, 员工活码 and 客户专属活码
        if (state.isCustomerEmploy()) {
            weEmpleCodeService.customerTempEmpleCodeCallBackHandle(state, customerId);
        } else {
            weEmpleCodeService.empleCodeCallBackHandle(state, customerId);
        }

    }

    /**
     * 检查客户是否已存在
     * 通过用户ID、外部用户ID和企业ID来判断客户关系是否已经存在
     *
     * @param customerId 客户Id
     * @return 如果客户关系存在则返回true，否则返回false
     */
    private boolean existCustomer(CustomerId customerId) {
        // 输入参数校验
        if (customerId.invalid()) {
            log.warn("Invalid input parameters: customerId={}", customerId);
            return false;
        }

        LambdaQueryWrapper<WeFlowerCustomerRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeFlowerCustomerRel::getCorpId, customerId.getCorpId());
        queryWrapper.eq(WeFlowerCustomerRel::getUserId, customerId.getUserId());
        queryWrapper.eq(WeFlowerCustomerRel::getExternalUserid, customerId.getExternalUserid());

        try {
            WeFlowerCustomerRel one = weFlowerCustomerRelService.getOne(queryWrapper);
            return one != null;
        } catch (Exception e) {
            log.error("查询客户是否存在异常: {}", ExceptionUtils.getStackTrace(e));
            return false; // 或者根据业务需求返回其他值
        }
    }


    /**
     * 处理新增联系人的密文明文映射
     * @param externalUserId 外部联系人id
     * @param corpId 企业id
     */
    private void handleDecrExternalUserId(String externalUserId, String corpId) {
        try {
            LockSelfBuildConfig lockSelfBuildConfig = lockSelfBuildConfigMapper.get(corpId);
            if (lockSelfBuildConfig == null) {
                log.info("[处理外部联系人id映射] 企业{}未配置selfBuild配置, 不进行解密", corpId);
                return;
            }
            WeCorpAccountService weCorpAccountService = SpringUtils.getBean(WeCorpAccountService.class);
            WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
            if (corpAccount == null) {
                log.info("[处理外部联系人id映射] 找不到企业配置, 不进行解密, corpId: {}", corpId);
                return;
            }

            String originExternalUserId = selfBuildApiService.decryptExternalUserId(externalUserId, corpAccount.getAgentId(), lockSelfBuildConfig.getDecryptExternalUserIdUrl());
            if(StringUtils.isBlank(originExternalUserId)){
                return;
            }

            weExternalUserIdMappingService.batchInsertOrUpdate(Collections.singletonList(new WeExternalUseridMapping(
                    corpId,
                    originExternalUserId,
                    externalUserId
            )));
        } catch (Exception e) {
            log.error("[处理新增外部联系人id映射] 处理异常, externalUserId:{}, corpId:{}, e: {}", externalUserId, corpId, ExceptionUtils.getStackTrace(e));
        }
    }


    private boolean checkParams(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName(), message.getUserId(), message.getExternalUserId())) {
            log.error("[add_external_contact]:回调数据不完整,message:{}", message);
            return false;
        }
        if (Boolean.FALSE.equals(redisCache.addLock(message.getUniqueKey(message.getExternalUserId()), "", Constants.CALLBACK_HANDLE_LOCK_TIME))) {
            log.info("[{}]添加客户事件回调,该回调已处理,不重复处理,message:{}", message.getChangeType(), message);
            // 不重复处理
            return false;
        }
        return true;
    }

}
