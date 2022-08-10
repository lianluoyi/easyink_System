package com.easywecom.wecom.strategy;

import cn.hutool.json.JSONObject;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.MessageType;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.ReflectUtil;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.WeMessagePush;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.WeMessagePushResultDTO;
import com.easywecom.wecom.mapper.WeMessagePushMapper;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 发送应用消息
 */
@Service
public class SendMessageToUserStrategy implements Strategy {

    @Autowired
    private WeMessagePushMapper weMessagePushMapper;

    @Autowired
    private WeMessagePushClient weMessagePushClient;

    @Override
    public void sendMessage(WeMessagePush weMessagePush, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException("corpId 不能为空");
        }
        HashMap<String, Object> map = Maps.newHashMap();
        JSONObject jsonObject = new JSONObject(weMessagePush.getMessageJson());
        Optional<MessageType> of = MessageType.of(weMessagePush.getMessageType());
        of.ifPresent(messageType -> map.put(messageType.getMessageType(), jsonObject));
        //发送消息
        WeMessagePushDTO weMessagePushDto = new WeMessagePushDTO();
        Optional.ofNullable(weMessagePush.getToUser()).ifPresent(userId -> {
            String tousers = Arrays.stream(userId.split(",")).collect(Collectors.joining("|"));
            weMessagePushDto.setTouser(tousers);
        });
        Optional.ofNullable(weMessagePush.getToParty()).ifPresent(partyId -> {
            String toPartys = Arrays.stream(partyId.split(",")).collect(Collectors.joining("|"));
            weMessagePushDto.setToparty(toPartys);
        });
        Optional.ofNullable(weMessagePush.getToTag()).ifPresent(tagId -> {
            String tagIds = Arrays.stream(tagId.split(",")).collect(Collectors.joining("|"));
            weMessagePushDto.setTotag(tagIds);
        });
        weMessagePushDto.setMsgtype(weMessagePush.getMessageType());

        //这个先写在配置文件中
        weMessagePushDto.setAgentid(weMessagePush.getAgentid());
        weMessagePushDto.setSafe(0);
        weMessagePushDto.setEnable_id_trans(0);
        weMessagePushDto.setEnable_duplicate_check(0);
        weMessagePushDto.setDuplicate_check_interval(1800L);


        //动态添加微信消息体属性和属性值信息
        WeMessagePushDTO target = (WeMessagePushDTO) ReflectUtil.getTarget(weMessagePushDto, map);
        WeMessagePushResultDTO weMessagePushResultDto = weMessagePushClient.sendMessageToUser(target, target.getAgentid().toString(), corpId);

        if (WeConstans.WE_SUCCESS_CODE.equals(weMessagePushResultDto.getErrcode())) {
            weMessagePush.setCreateTime(DateUtils.getNowDate());
            weMessagePush.setDelFlag(0);
            weMessagePush.setMessagePushId(SnowFlakeUtil.nextId());

            //存储返回结果信息
            weMessagePush.setInvaliduser(weMessagePushResultDto.getInvaliduser());
            weMessagePush.setInvalidparty(weMessagePushResultDto.getInvalidparty());
            weMessagePush.setInvalidtag(weMessagePushResultDto.getInvalidtag());
        }

        weMessagePushMapper.insert(weMessagePush);

    }


}
