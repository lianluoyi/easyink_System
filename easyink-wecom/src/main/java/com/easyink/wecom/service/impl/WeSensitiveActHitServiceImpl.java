package com.easyink.wecom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.RootEntity;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.WeSensitiveActEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeSensitiveAct;
import com.easyink.wecom.domain.WeSensitiveActHit;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.wecom.domain.query.sensitiveact.WeSensitiveActQuery;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeSensitiveActHitMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeSensitiveActHitService;
import com.easyink.wecom.service.WeSensitiveActService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author admin
 * @version 1.0
 * @date 2021/1/13 9:05
 */
@Service
@Slf4j
public class WeSensitiveActHitServiceImpl extends ServiceImpl<WeSensitiveActHitMapper, WeSensitiveActHit> implements WeSensitiveActHitService {
    @Autowired
    private WeSensitiveActService weSensitiveActService;
    @Autowired
    private WeUserMapper weUserMapper;
    @Autowired
    private WeCustomerMapper weCustomerMapper;
    @Autowired
    private WeSensitiveActHitMapper weSensitiveActHitMapper;
    private final WeCorpAccountService weCorpAccountService;

    public WeSensitiveActHitServiceImpl(WeCorpAccountService weCorpAccountService) {
        this.weCorpAccountService = weCorpAccountService;
    }

    @Override
    public WeSensitiveActHit selectWeSensitiveActHitById(Long id) {
        return getById(id);
    }
    /**
     * 更新敏感行为信息
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHistorySensitive() {
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        if (CollectionUtils.isEmpty(weCorpAccountList)) {
            log.info("[更新敏感行为信息] 当前无可更新信息的企业，请检查配置或联系管理员");
            return;
        }
        weCorpAccountList.forEach(corpAccount -> {
            String corpId = corpAccount.getCorpId();
            log.info("[更新敏感行为信息] 开始更新敏感行为信息，当前企业corpId:{}", corpId);
            try {
                // 最终需要更新数据的列表
                List<WeSensitiveActHit> updateList = new ArrayList<>();
                // 获取企业下的所有敏感行为记录信息
                List<WeSensitiveActHit> actHits = list(new LambdaQueryWrapper<WeSensitiveActHit>().eq(WeSensitiveActHit::getCorpId, corpId));
                if (CollectionUtils.isEmpty(actHits)) {
                    log.info("[更新敏感行为信息] 当前企业未开通会话存档或无敏感行为记录，当前企业corpId:{}", corpId);
                    return;
                }
                for (WeSensitiveActHit actHit : actHits) {
                    // 若两个数据都不为空， 且头像信息都不为空，则跳过
                    if (StringUtils.isNotBlank(actHit.getOperator()) && StringUtils.isNotBlank(actHit.getOperateTarget())) {
                        if (StringUtils.isNotBlank(JSONObject.parseObject(actHit.getOperateTarget()).getString("avatar")) && StringUtils.isNotBlank(JSONObject.parseObject(actHit.getOperator()).getString("avatar"))) {
                            continue;
                        }
                    }
                    // 获取操作对象（客户）信息
                    actHit.setOperateTarget(this.getUserOrCustomerName(WeConstans.ID_TYPE_EX, actHit.getOperateTargetId(), corpId));
                    // 获取操作人（员工）信息
                    actHit.setOperator(this.getUserOrCustomerName(WeConstans.ID_TYPE_USER, actHit.getOperatorId(), corpId));
                    // 添加进需要更新的列表
                    updateList.add(actHit);
                }
                if (CollectionUtils.isEmpty(updateList)) {
                    log.info("[更新敏感行为信息] 当前企业无需要更新的敏感行为信息，当前企业corpId:{}", corpId);
                    return;
                }
                // 更新数据
                this.saveOrUpdateBatch(updateList);
                log.info("[更新敏感行为信息] 更新敏感行为信息结束，当前更新企业corpId:{}, 更新的信息:{}", corpId, updateList);
            } catch (Exception e) {
                log.info("[更新敏感行为信息] 更新敏感行为信息异常，异常的企业corpId:{}, 异常原因:{}", corpId, ExceptionUtils.getStackTrace(e));
            }
        });
    }

    @Override
    @DataScope
    public List<WeSensitiveActHit> selectWeSensitiveActHitList(RootEntity rootEntity, WeSensitiveActQuery actQuery) {
        if (ObjectUtils.isEmpty(rootEntity.getParams().get("corpId"))){
            return new ArrayList<>();
        }
        return weSensitiveActHitMapper.listOfWeSensitiveActHit(rootEntity, actQuery);
    }

    @Override
    public boolean insertWeSensitiveActHit(WeSensitiveActHit weSensitiveActHit) {
        if (org.apache.commons.lang3.StringUtils.isBlank(weSensitiveActHit.getCreateBy())) {
            weSensitiveActHit.setCreateBy(LoginTokenService.getUsername());
        }
        weSensitiveActHit.setCreateTime(DateUtils.getNowDate());
        return saveOrUpdate(weSensitiveActHit);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void hitWeSensitiveAct(String corpId, List<JSONObject> chatDataList) {
        if (StringUtils.isEmpty(corpId)) {
            return;
        }
        List<WeSensitiveActHit> saveList = chatDataList.stream().filter(chatData -> {
            String type = chatData.getString(WeConstans.MSG_TYPE);
            String roomId = chatData.getString(WeConstans.ROOMID);
            String from = chatData.getString(WeConstans.FROM);
            //过滤掉群消息
            if (StringUtils.isNotBlank(roomId)) {
                return false;
            }
            //过滤名片消息且非员工主动发送
            if (WeConstans.ID_TYPE_USER.equals(com.easyink.common.utils.StringUtils.weCustomTypeJudgment(from)) && WeSensitiveActEnum.SEND_CARD.getName().equals(type)) {
                return true;
            }
            return WeSensitiveActEnum.SEND_REDPACK.getName().equals(type) || WeSensitiveActEnum.SEND_EXTERNAL_REDPACK.getName().equals(type);
        }).map(chatData -> {
            WeSensitiveActHit weSensitiveActHit = new WeSensitiveActHit();
            WeSensitiveAct weSensitiveAct = getSensitiveActType(chatData.getString(WeConstans.MSG_TYPE), corpId);
            if (weSensitiveAct != null && weSensitiveAct.getEnableFlag() == 1) {
                weSensitiveActHit.setSensitiveAct(weSensitiveAct.getActName());
                weSensitiveActHit.setSensitiveActId(weSensitiveAct.getId());
                weSensitiveActHit.setCreateTime(new Date(chatData.getLong(WeConstans.MSG_TIME)));
                weSensitiveActHit.setCreateBy("");
                String operatorId = chatData.getString(WeConstans.FROM);
                String operatorTargetId = chatData.getJSONArray(WeConstans.TO_LIST).getString(0);
                weSensitiveActHit.setOperatorId(operatorId);
                weSensitiveActHit.setOperateTargetId(operatorTargetId);
                weSensitiveActHit.setCorpId(corpId);
                setUserOrCustomerInfo(weSensitiveActHit);
                return weSensitiveActHit;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        saveOrUpdateBatch(saveList);
    }

    private WeSensitiveAct getSensitiveAct(String type, String corpId) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(corpId)){
            return null;
        }
        WeSensitiveAct weSensitiveAct = new WeSensitiveAct();
        weSensitiveAct.setActName(type);
        weSensitiveAct.setCorpId(corpId);
        List<WeSensitiveAct> list = weSensitiveActService.selectWeSensitiveActList(weSensitiveAct);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public WeSensitiveAct getSensitiveActType(String msgType, String corpId) {
        String type;
        if (WeSensitiveActEnum.SEND_CARD.getName().equals(msgType)) {
            type = WeSensitiveActEnum.SEND_CARD.getInfo();
        } else if (WeSensitiveActEnum.SEND_REDPACK.getName().equals(msgType) || WeSensitiveActEnum.SEND_EXTERNAL_REDPACK.getName().equals(msgType)) {
            type = WeSensitiveActEnum.SEND_REDPACK.getInfo();
        } else {
            type = WeSensitiveActEnum.DELETE.getInfo();
        }
        return getSensitiveAct(type, corpId);
    }

    @Override
    public void setUserOrCustomerInfo(WeSensitiveActHit weSensitiveActHit) {
        if (ObjectUtils.isEmpty(weSensitiveActHit) && StringUtils.isEmpty(weSensitiveActHit.getCorpId())){
            return;
        }
        int operatorType = com.easyink.common.utils.StringUtils.weCustomTypeJudgment(weSensitiveActHit.getOperatorId());
        int operatorTargetType = com.easyink.common.utils.StringUtils.weCustomTypeJudgment(weSensitiveActHit.getOperateTargetId());
        weSensitiveActHit.setOperator(getUserOrCustomerName(operatorType, weSensitiveActHit.getOperatorId(), weSensitiveActHit.getCorpId()));
        weSensitiveActHit.setOperateTarget(getUserOrCustomerName(operatorTargetType, weSensitiveActHit.getOperateTargetId(), weSensitiveActHit.getCorpId()));
    }

    @Override
    public void hitWeSensitiveAct1(String corpId, List<ChatInfoVO> chatDataList) {
        if (StringUtils.isEmpty(corpId)) {
            return;
        }
        List<WeSensitiveActHit> saveList = chatDataList.stream().filter(chatData -> {
            String type = chatData.getMsgtype();
            String roomId = chatData.getRoomid();
            String from = chatData.getFrom();
            //过滤掉群消息
            if (StringUtils.isNotBlank(roomId)) {
                return false;
            }
            //过滤名片消息且非员工主动发送
            if (WeConstans.ID_TYPE_USER.equals(com.easyink.common.utils.StringUtils.weCustomTypeJudgment(from)) && WeSensitiveActEnum.SEND_CARD.getName().equals(type)) {
                return true;
            }
            return WeSensitiveActEnum.SEND_REDPACK.getName().equals(type) || WeSensitiveActEnum.SEND_EXTERNAL_REDPACK.getName().equals(type);
        }).map(chatData -> {
            WeSensitiveActHit weSensitiveActHit = new WeSensitiveActHit();
            WeSensitiveAct weSensitiveAct = getSensitiveActType(chatData.getMsgtype(), corpId);
            if (weSensitiveAct != null && weSensitiveAct.getEnableFlag() == 1) {
                weSensitiveActHit.setSensitiveAct(weSensitiveAct.getActName());
                weSensitiveActHit.setSensitiveActId(weSensitiveAct.getId());
                weSensitiveActHit.setCreateTime(new Date(chatData.getMsgtime()));
                weSensitiveActHit.setCreateBy("");
                String operatorId = chatData.getFrom();
                String operatorTargetId = chatData.getTolist().get(0);
                weSensitiveActHit.setOperatorId(operatorId);
                weSensitiveActHit.setOperateTargetId(operatorTargetId);
                weSensitiveActHit.setCorpId(corpId);
                setUserOrCustomerInfo(weSensitiveActHit);
                return weSensitiveActHit;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        saveOrUpdateBatch(saveList);
    }

    private String getUserOrCustomerName(int type, String userId, String corpId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(corpId)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        //获取发送人信息
        if (WeConstans.ID_TYPE_USER.equals(type)) {
            //成员信息
            WeUser weUser = weUserMapper.selectOne(new LambdaQueryWrapper<WeUser>()
                    .eq(WeUser::getCorpId,corpId)
                    .eq(WeUser::getUserId, userId));
            if (weUser != null) {
                jsonObject.put("avatar", weUser.getAvatarMediaid());
                jsonObject.put("name", weUser.getName());
                return jsonObject.toJSONString();
            }
        } else if (WeConstans.ID_TYPE_EX.equals(type)) {
            //获取外部联系人信息
            WeCustomer weCustomer = weCustomerMapper.selectWeCustomerById(userId, corpId);
            if (weCustomer != null) {
                jsonObject.put("avatar", weCustomer.getAvatar());
                jsonObject.put("name", weCustomer.getName());
                jsonObject.put("type", weCustomer.getType());
                jsonObject.put("corpName", weCustomer.getCorpName());
                jsonObject.put("corpFullName", weCustomer.getCorpFullName());
                return jsonObject.toJSONString();
            }
        } else if (WeConstans.ID_TYPE_MACHINE.equals(type)) {
            //拉去机器人信息暂不处理
        }
        return null;
    }
}
