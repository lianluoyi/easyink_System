package com.easywecom.wecom.factory.impl.customergroup;

import com.easywecom.common.constant.Constants;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.common.enums.CallbackEventUpdateDetail;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeGroupMember;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.WeGroupCodeActualService;
import com.easywecom.wecom.service.WeGroupMemberService;
import com.easywecom.wecom.service.WeGroupService;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleHitGroupRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description 客户群变更事件
 * @date 2021/1/20 0:39
 **/
@Slf4j
@Component("update")
public class WeCallBackUpdateGroupImpl extends WeEventStrategy {
    @Autowired
    private WeGroupService weGroupService;
    @Autowired
    private WeGroupCodeActualService groupCodeActualService;
    @Autowired
    private WeGroupMemberService weGroupMemberService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private WeAutoTagRuleHitGroupRecordService weAutoTagRuleHitGroupRecordService;


    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isAnyBlank(message.getToUserName(), message.getChatId())) {
            log.error("[update]客户群更新事件回调,参数缺失,message:{}", message);
            return;
        }
        if (!redisCache.addLock(message.getUniqueKey(message.getChatId()), "", Constants.CALLBACK_HANDLE_LOCK_TIME)) {
            log.info("[update]客户群更新事件回调,该回调已处理,不重复处理,message:{}", message);
            // 不重复处理
            return;
        }
        try {
            // 接收到回调后与本地比较的群成员变动列表
            List<WeGroupMember> changesWeGroupMembers = weGroupService.updateWeGroup(message.getToUserName(), message.getChatId());
            String updateDetail = message.getUpdateDetail();
            String joinScene = message.getJoinScene();
            //判断是否为新增成员且扫码入群
            if (CallbackEventUpdateDetail.ADD_MEMBER.getType().equals(updateDetail) && WeConstans.CODE_JOIN_SCENE.equals(joinScene)) {
                String memberChangeCnt = message.getMemberChangeCnt();
                // 添加成员，该群的实际群活码扫码入群人数增加
                if (StringUtils.isNotEmpty(memberChangeCnt)) {
                    groupCodeActualService.updateScanTimesByChatId(message.getChatId(), Integer.parseInt(memberChangeCnt));
                }
            }
            if (CallbackEventUpdateDetail.ADD_MEMBER.getType().equals(updateDetail)) {
                // 判断是否需要打标签
                List<String> newJoinCustomerIdList = changesWeGroupMembers.stream().filter(item -> WeConstans.WE_GROUP_MEMBER_TYPE_CUSTOMER.equals(item.getJoinType()))
                        .map(WeGroupMember::getUserId).collect(Collectors.toList());
                log.info("需要打标签的客户id列表: {}", newJoinCustomerIdList);
                weAutoTagRuleHitGroupRecordService.makeTagToNewCustomer(message.getChatId(), newJoinCustomerIdList, message.getToUserName());
            }
            //退群事件
            if (CallbackEventUpdateDetail.DEL_MEMBER.getType().equals(updateDetail)) {
                //同步群成员列表
                weGroupMemberService.synchWeGroupMember(message.getChatId(), message.getToUserName());
            }
        } catch (Exception e) {
            log.error("update>>>>>>>>>param:{},ex:{}", message.getChatId(), ExceptionUtils.getStackTrace(e));
        }
    }

}
