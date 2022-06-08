package com.easywecom.wecom.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.MessageType;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.ReflectUtil;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.WeGroup;
import com.easywecom.wecom.domain.WeMessagePush;
import com.easywecom.wecom.domain.dto.WeMessagePushGroupDTO;
import com.easywecom.wecom.domain.dto.WeMessagePushResultDTO;
import com.easywecom.wecom.mapper.WeMessagePushMapper;
import com.easywecom.wecom.service.WeGroupService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 应用推送消息
 */
@Service
public class SendMessageToUserGroupStrategy implements Strategy {

    @Autowired
    private WeMessagePushMapper weMessagePushMapper;

    @Autowired
    private WeMessagePushClient weMessagePushClient;

    @Autowired
    private WeGroupService weGroupService;

    @Override
    public void sendMessage(WeMessagePush weMessagePush, String corpId) {
        if (com.easywecom.common.utils.StringUtils.isBlank(corpId)) {
            throw new CustomException("corpId 不能为空");
        }
        HashMap<String, Object> map = Maps.newHashMap();
        JSONObject jsonObject = new JSONObject(weMessagePush.getMessageJson());
        Optional<MessageType> of = MessageType.of(weMessagePush.getMessageType());
        of.ifPresent(messageType -> map.put(messageType.getMessageType(), jsonObject));
        //根据员工id列表查询所有的群信息
        List<String> strings = Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(weMessagePush.getToUser(), WeConstans.COMMA));
        List<String> chatIds = Lists.newArrayList();
        strings.forEach(s -> {
            List<WeGroup> groups = weGroupService
                    .list(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getOwner, s));
            //发送消息到群聊
            if (CollUtil.isNotEmpty(groups)) {
                groups.forEach(d -> {
                    WeMessagePushGroupDTO weMessagePushGroupDto = new WeMessagePushGroupDTO();
                    weMessagePushGroupDto.setChatid(d.getChatId());
                    weMessagePushGroupDto.setMsgtype(weMessagePush.getMessageType());
                    weMessagePushGroupDto.setSafe(0);
                    //动态添加微信消息体属性和属性值信息
                    WeMessagePushGroupDTO target = (WeMessagePushGroupDTO) ReflectUtil.getTarget(weMessagePushGroupDto, map);
                    WeMessagePushResultDTO weMessagePushResultDto = weMessagePushClient.sendMessageToUserGroup(target, corpId);

                    if (WeConstans.WE_SUCCESS_CODE.equals(weMessagePushResultDto.getErrcode())) {
                        //保存发送的群消息
                        chatIds.add(d.getChatId());
                    }

                });
            }

        });

        weMessagePush.setCreateTime(DateUtils.getNowDate());
        weMessagePush.setDelFlag(0);
        weMessagePush.setMessagePushId(SnowFlakeUtil.nextId());
        weMessagePush.setChatId(CollUtil.isNotEmpty(chatIds) ? String.join(",", chatIds) : null);

        weMessagePushMapper.insert(weMessagePush);

    }


}
