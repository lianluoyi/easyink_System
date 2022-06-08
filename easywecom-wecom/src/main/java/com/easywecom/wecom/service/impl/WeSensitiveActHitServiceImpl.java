package com.easywecom.wecom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.annotation.DataScope;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.RootEntity;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.WeSensitiveActEnum;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeSensitiveAct;
import com.easywecom.wecom.domain.WeSensitiveActHit;
import com.easywecom.common.core.domain.conversation.ChatInfoVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.WeCustomerMapper;
import com.easywecom.wecom.mapper.WeSensitiveActHitMapper;
import com.easywecom.wecom.mapper.WeUserMapper;
import com.easywecom.wecom.service.WeSensitiveActHitService;
import com.easywecom.wecom.service.WeSensitiveActService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
public class WeSensitiveActHitServiceImpl extends ServiceImpl<WeSensitiveActHitMapper, WeSensitiveActHit> implements WeSensitiveActHitService {
    @Autowired
    private WeSensitiveActService weSensitiveActService;
    @Autowired
    private WeUserMapper weUserMapper;
    @Autowired
    private WeCustomerMapper weCustomerMapper;
    @Autowired
    private WeSensitiveActHitMapper weSensitiveActHitMapper;

    @Override
    public WeSensitiveActHit selectWeSensitiveActHitById(Long id) {
        return getById(id);
    }

    @Override
    @DataScope
    public List<WeSensitiveActHit> selectWeSensitiveActHitList(RootEntity rootEntity) {
        if (ObjectUtils.isEmpty(rootEntity.getParams().get("corpId"))){
            return new ArrayList<>();
        }
        return weSensitiveActHitMapper.listOfWeSensitiveActHit(rootEntity);
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
            if (WeConstans.ID_TYPE_USER.equals(com.easywecom.common.utils.StringUtils.weCustomTypeJudgment(from)) && WeSensitiveActEnum.SEND_CARD.getName().equals(type)) {
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
        int operatorType = com.easywecom.common.utils.StringUtils.weCustomTypeJudgment(weSensitiveActHit.getOperatorId());
        int operatorTargetType = com.easywecom.common.utils.StringUtils.weCustomTypeJudgment(weSensitiveActHit.getOperateTargetId());
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
            if (WeConstans.ID_TYPE_USER.equals(com.easywecom.common.utils.StringUtils.weCustomTypeJudgment(from)) && WeSensitiveActEnum.SEND_CARD.getName().equals(type)) {
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
