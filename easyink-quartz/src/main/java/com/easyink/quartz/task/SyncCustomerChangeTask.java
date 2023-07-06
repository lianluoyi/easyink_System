package com.easyink.quartz.task;

import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.impl.customer.WeCallBackAddExternalContactImpl;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.utils.redis.CustomerRedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类名: 同步客户变更任务
 * (由于客户变更edit_external_contact 回调因为 批量打标签和其他特殊场景,有时候会短时间内回调较多，
 * 所以不在回调时马上处理,而是定时任务处理前段时间的客户遍及变更)
 *
 * @author : silver_chariot
 * @update V1.29.2 起, 新增客户的回调 add_external_contact 也在此任务一并执行
 * @date : 2023/6/8 17:24
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class SyncCustomerChangeTask {

    @Resource(name = "customerRedisCache")
    private CustomerRedisCache customerRedisCache;
    /**
     * 新增客户回调类型
     */
    private final static String ADD_CONTACT_TYPE = "add_external_contact";
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerService weCustomerService;
    @Resource(name = "syncEditCustomerExecutor")
    private ThreadPoolTaskExecutor syncEditCustomerExecutor;
    @Resource(name = "add_external_contact")
    private WeCallBackAddExternalContactImpl weCallBackAddExternalContact;

    /**
     * 执行任务 cron : 0/30 * * * * ?
     */
    public void execute() {
        // 1. 获取所有企业
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        if (CollectionUtils.isEmpty(weCorpAccountList)) {
            return;
        }
        for (WeCorpAccount corpAccount : weCorpAccountList) {
            if(corpAccount == null || StringUtils.isEmpty(corpAccount.getCorpId())) {
                continue;
            }
            syncEditCustomerExecutor.execute(()->{
                try {
                    // 2. 获取所有未同步的消息 （获取后马上删除,避免下一次任务执行时本任务还没结束,处理了相同的客户)
                    List<CustomerRedisCache.RedisCustomerModel> callbackCustomerModels = customerRedisCache.getCallbackCustomerModel(corpAccount.getCorpId());
                    if(CollectionUtils.isEmpty(callbackCustomerModels)) {
                        return;
                    }
                    for ( CustomerRedisCache.RedisCustomerModel model : callbackCustomerModels) {
                        try {
                            // 3. 依次执行原 新增/变更回调处理
                            if (isAddContact(model.getMessage())) {
                                weCallBackAddExternalContact.addHandle(model.getMessage());
                            } else {
                                weCustomerService.updateExternalContactV2(corpAccount.getCorpId(), model.getUserId(), model.getExternalUserId());
                            }
                        } catch (Exception e) {
                            log.error("[同步回调变更客户]更新客户异常,corpId:{}, userId:{},exuserId:{},msg:{},e :{}", corpAccount.getCorpId(), model.getUserId(), model.getExternalUserId(),model.getMessage(), ExceptionUtils.getStackTrace(e));
                        }
                    }
                    log.info("[同步回调变更客户] 同步成功{}个客户-员工信息,corpId:{}", callbackCustomerModels.size(), corpAccount.getCorpId());
                } catch (Exception e) {
                    log.error("[同步回调变更客户] 执行异常,corpID:{} e :{} ", corpAccount.getExternalCorpId(), ExceptionUtils.getStackTrace(e));
                }
            });

        }


    }

    /**
     * 是否是新增客户的回调
     *
     * @param message 回调消息
     * @return true 是 false 否
     */
    private boolean isAddContact(WxCpXmlMessageVO message) {
        if (message == null) {
            return false;
        }
        return ADD_CONTACT_TYPE.equals(message.getChangeType());
    }

}
