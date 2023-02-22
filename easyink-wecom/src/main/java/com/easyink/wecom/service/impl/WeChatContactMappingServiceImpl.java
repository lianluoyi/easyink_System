package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeChatContactMapping;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.wecom.mapper.WeChatContactMappingMapper;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.WeChatContactMappingService;
import com.easyink.wecom.service.WeConversationArchiveService;
import com.easyink.wecom.service.WeGroupMemberService;
import com.easyink.wecom.service.WeGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天关系映射Service业务层处理
 *
 * @author admin
 * @date 2020-12-27
 */
@Slf4j
@Service
public class WeChatContactMappingServiceImpl extends ServiceImpl<WeChatContactMappingMapper, WeChatContactMapping> implements WeChatContactMappingService {
    @Autowired
    private WeChatContactMappingMapper weChatContactMappingMapper;
    @Autowired
    private WeUserMapper weUserMapper;
    @Autowired
    private WeCustomerMapper weCustomerMapper;
    @Autowired
    private WeConversationArchiveService weConversationArchiveService;
    @Autowired
    private WeGroupService weGroupService;
    @Autowired
    private WeGroupMemberService weGroupMemberService;

    /**
     * 查询聊天关系映射
     *
     * @param id 聊天关系映射ID
     * @return 聊天关系映射
     */
    @Override
    public WeChatContactMapping selectWeChatContactMappingById(Long id) {
        return weChatContactMappingMapper.selectWeChatContactMappingById(id);
    }

    /**
     * 查询聊天关系映射列表
     *
     * @param weChatContactMapping 聊天关系映射
     * @return 聊天关系映射
     */
    @DataScope
    @Override
    public List<WeChatContactMapping> selectWeChatContactMappingList(WeChatContactMapping weChatContactMapping) {
        if (ObjectUtils.isEmpty(weChatContactMapping) || StringUtils.isEmpty(weChatContactMapping.getCorpId())) {
            return new ArrayList<>();
        }
        List<WeChatContactMapping> weChatMappingList = weChatContactMappingMapper.selectWeChatContactMappingList(weChatContactMapping);
        if (CollectionUtils.isEmpty(weChatMappingList)) {
            return weChatMappingList;
        }
        Iterator<WeChatContactMapping> it = weChatMappingList.iterator();
        while (it.hasNext()) {
            WeChatContactMapping item = it.next();
            if (StringUtils.isNotEmpty(item.getReceiveId())) {
                if (WeConstans.ID_TYPE_USER.equals(item.getIsCustom())) {
                    //成员信息
                    WeUser weUser = weUserMapper.selectOne(new LambdaQueryWrapper<WeUser>()
                            .eq(WeUser::getCorpId, item.getCorpId())
                            .eq(WeUser::getUserId, item.getReceiveId()));
                    if (weUser != null) {
                        item.setReceiveWeUser(weUser);
                    } else {
                        it.remove();
                        continue;
                    }
                } else if (WeConstans.ID_TYPE_EX.equals(item.getIsCustom())) {
                    //获取外部联系人信息
                    WeCustomer weCustomer = weCustomerMapper.selectWeCustomerById(item.getReceiveId(), weChatContactMapping.getCorpId());
                    if (null == weCustomer) {
                        it.remove();
                        continue;
                    }
                    item.setReceiveWeCustomer(weCustomer);
                }
                if (org.apache.commons.lang3.StringUtils.isNoneBlank(item.getFromId(), item.getReceiveId())) {
                    item.setFinalChatContext(weConversationArchiveService.getFinalChatContactInfo(item.getFromId(), item.getReceiveId(), weChatContactMapping.getCorpId()));
                }
            } else if (StringUtils.isNotEmpty(item.getRoomId())) {
                //获取群信息
                WeGroup weGroup = weGroupService.getOne(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getChatId, item.getRoomId()).last("limit 1"));
                //查询群成员头像
                if (weGroup != null) {
                    List<WeGroupMemberDTO> weGroupMemberDtos = weGroupMemberService.selectWeGroupMemberListByChatId(item.getRoomId());
                    String roomAvatar = weGroupMemberDtos.stream()
                            .map(WeGroupMemberDTO::getMemberAvatar)
                            .filter(StringUtils::isNotEmpty)
                            .limit(9)
                            .collect(Collectors.joining(","));
                    weGroup.setAvatar(roomAvatar);
                    item.setRoomInfo(weGroup);
                } else {
                    it.remove();
                    continue;
                }
                if (org.apache.commons.lang3.StringUtils.isNoneBlank(item.getFromId(), item.getRoomId())) {
                    item.setFinalChatContext(weConversationArchiveService.getFinalChatRoomContactInfo(item.getFromId(), item.getRoomId(), weChatContactMapping.getCorpId()));
                }
            }
        }
        Collections.sort(weChatMappingList);
        return weChatMappingList;
    }

    /**
     * 新增聊天关系映射
     *
     * @param weChatContactMapping 聊天关系映射
     * @return 结果
     */
    @Override
    public int insertWeChatContactMapping(WeChatContactMapping weChatContactMapping) {
        List<WeChatContactMapping> list = weChatContactMappingMapper.selectWeChatContactMappingList(weChatContactMapping);
        if (CollUtil.isEmpty(list)) {
            return weChatContactMappingMapper.insertWeChatContactMapping(weChatContactMapping);
        }
        return 0;
    }

    /**
     * 修改聊天关系映射
     *
     * @param weChatContactMapping 聊天关系映射
     * @return 结果
     */
    @Override
    public int updateWeChatContactMapping(WeChatContactMapping weChatContactMapping) {
        return weChatContactMappingMapper.updateWeChatContactMapping(weChatContactMapping);
    }

    /**
     * 批量删除聊天关系映射
     *
     * @param ids 需要删除的聊天关系映射ID
     * @return 结果
     */
    @Override
    public int deleteWeChatContactMappingByIds(Long[] ids) {
        return weChatContactMappingMapper.deleteWeChatContactMappingByIds(ids);
    }

    /**
     * 删除聊天关系映射信息
     *
     * @param id 聊天关系映射ID
     * @return 结果
     */
    @Override
    public int deleteWeChatContactMappingById(Long id) {
        return weChatContactMappingMapper.deleteWeChatContactMappingById(id);
    }

    /**
     * 需后期优化
     *
     * @param corpId 企业id
     * @param query  会话列表
     * @return 会话联系人列表结果
     */
    @Override
    public List<JSONObject> saveWeChatContactMapping(String corpId, List<JSONObject> query) {
        List<JSONObject> resultList = new ArrayList<>();
        if (StringUtils.isEmpty(corpId)) {
            return resultList;
        }
        query.stream().filter(chatData -> StringUtils.isNotEmpty(chatData.getString(WeConstans.FROM))).forEach(chatData -> {
            //发送人映射数据
            WeChatContactMapping fromWeChatContactMapping = new WeChatContactMapping();
            fromWeChatContactMapping.setCorpId(corpId);
            String fromId = chatData.getString(WeConstans.FROM);
            //发送人类型
            int fromType = StringUtils.weCustomTypeJudgment(fromId);
            fromWeChatContactMapping.setFromId(fromId);
            getUserOrCustomerInfo(chatData, fromId, fromType, WeConstans.FROMM_INFO, corpId);


            JSONArray tolist = chatData.getJSONArray(WeConstans.TO_LIST);
            if (CollUtil.isNotEmpty(tolist)) {
                //如果是单聊，tolist唯一，只有一个接收人，并判断接收人的类型
                if (StringUtils.isEmpty(chatData.getString(WeConstans.ROOMID))) {
                    String idStr = String.valueOf(tolist.get(0));
                    //接收人类型
                    int reveiceType = StringUtils.weCustomTypeJudgment(idStr);
                    fromWeChatContactMapping.setReceiveId(idStr);
                    //获取接收人信息
                    getUserOrCustomerInfo(chatData, idStr, reveiceType, WeConstans.TO_LIST_INFO, corpId);
                    fromWeChatContactMapping.setIsCustom(reveiceType);
                } else {
                    fromWeChatContactMapping.setRoomId(chatData.getString(WeConstans.ROOMID));
                    WeGroup weGroup = weGroupService.getOne(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getChatId, chatData.getString(WeConstans.ROOMID)).last("limit 1"));
                    chatData.put(WeConstans.ROOM_INFO, JSON.parse(JSON.toJSONString(weGroup)));
                }
            }
            //接收人映射数据
            WeChatContactMapping reveiceWeChatContactMapping = from2ReveiceData(fromType, fromWeChatContactMapping);
            insertWeChatContactMapping(fromWeChatContactMapping);
            insertWeChatContactMapping(reveiceWeChatContactMapping);
            resultList.add(chatData);
        });
        return resultList;
    }

    @Override
    public List<ChatInfoVO> saveWeChatContactMapping1(String corpId, List<ChatInfoVO> query) {
        List<ChatInfoVO> resultList = new ArrayList<>();
        if (StringUtils.isEmpty(corpId)) {
            return resultList;
        }
        query.stream().filter(chatData -> StringUtils.isNotEmpty(chatData.getFrom())).forEach(chatData -> {
            //发送人映射数据
            WeChatContactMapping fromWeChatContactMapping = new WeChatContactMapping();
            fromWeChatContactMapping.setCorpId(corpId);
            String fromId = chatData.getFrom();
            //发送人类型
            int fromType = StringUtils.weCustomTypeJudgment(fromId);
            fromWeChatContactMapping.setFromId(fromId);
//            getUserOrCustomerInfo(chatData, fromId, fromType, WeConstans.FROMM_INFO, corpId);
            getUserOrCustomerInfo1(chatData, fromId, fromType, WeConstans.FROMM_INFO, corpId);


            List<String> tolist = chatData.getTolist();
            if (CollUtil.isNotEmpty(tolist)) {
                //如果是单聊，tolist唯一，只有一个接收人，并判断接收人的类型
                if (StringUtils.isEmpty(chatData.getRoomid())) {
                    String idStr = String.valueOf(tolist.get(0));
                    //接收人类型
                    int reveiceType = StringUtils.weCustomTypeJudgment(idStr);
                    fromWeChatContactMapping.setReceiveId(idStr);
                    //获取接收人信息
                    getUserOrCustomerInfo1(chatData, idStr, reveiceType, WeConstans.TO_LIST_INFO, corpId);
                    fromWeChatContactMapping.setIsCustom(reveiceType);
                } else {
                    fromWeChatContactMapping.setRoomId(chatData.getRoomid());
                    WeGroup weGroup = weGroupService.getOne(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getChatId, chatData.getRoomid()).last("limit 1"));
                    chatData.setRoomInfo(JSON.parse(JSON.toJSONString(weGroup)));
                }
            }
            //接收人映射数据
            WeChatContactMapping reveiceWeChatContactMapping = from2ReveiceData(fromType, fromWeChatContactMapping);
            insertWeChatContactMapping(fromWeChatContactMapping);
            insertWeChatContactMapping(reveiceWeChatContactMapping);
            resultList.add(chatData);
        });
        return resultList;
    }

    private void getUserOrCustomerInfo1(ChatInfoVO chatData, String fromId, int fromType, String key, String corpId) {
        if (StringUtils.isEmpty(fromId) || StringUtils.isEmpty(key) || StringUtils.isEmpty(corpId)) {
            return;
        }
        //获取发送人信息
        if (WeConstans.ID_TYPE_USER.equals(fromType)) {
            //成员信息
            WeUser weUser = weUserMapper.selectOne(new LambdaQueryWrapper<WeUser>()
                    .eq(WeUser::getCorpId, corpId)
                    .eq(WeUser::getUserId, fromId));
            if (key.equals(WeConstans.TO_LIST_INFO)) {
                chatData.setToListInfo(JSON.parse(JSON.toJSONString(weUser)));
            } else if (key.equals(WeConstans.FROMM_INFO)) {
                chatData.setFromInfo(JSON.parse(JSON.toJSONString(weUser)));
            }
        } else if (WeConstans.ID_TYPE_EX.equals(fromType)) {
            //获取外部联系人信息
            WeCustomer weCustomer = weCustomerMapper.selectOne(new LambdaQueryWrapper<WeCustomer>()
                    .eq(WeCustomer::getCorpId, corpId)
                    .eq(WeCustomer::getExternalUserid, fromId)
                    .last(GenConstants.LIMIT_1));
            if (key.equals(WeConstans.TO_LIST_INFO)) {
                chatData.setToListInfo(JSON.parse(JSON.toJSONString(weCustomer)));
            } else if (key.equals(WeConstans.FROMM_INFO)) {
                chatData.setFromInfo(JSON.parse(JSON.toJSONString(weCustomer)));
            }
        } else if (WeConstans.ID_TYPE_MACHINE.equals(fromType)) {
            //拉去机器人信息暂不处理
        }
    }

    private void getUserOrCustomerInfo(JSONObject chatData, String fromId, int fromType, String key, String corpId) {
        if (StringUtils.isEmpty(fromId) || StringUtils.isEmpty(key) || StringUtils.isEmpty(corpId)) {
            return;
        }
        //获取发送人信息
        if (WeConstans.ID_TYPE_USER.equals(fromType)) {
            //成员信息
            WeUser weUser = weUserMapper.selectOne(new LambdaQueryWrapper<WeUser>()
                    .eq(WeUser::getCorpId, corpId)
                    .eq(WeUser::getUserId, fromId));
            chatData.put(key, JSON.parse(JSON.toJSONString(weUser)));
        } else if (WeConstans.ID_TYPE_EX.equals(fromType)) {
            //获取外部联系人信息
            WeCustomer weCustomer = weCustomerMapper.selectOne(new LambdaQueryWrapper<WeCustomer>().
                    eq(WeCustomer::getCorpId, corpId)
                    .eq(WeCustomer::getExternalUserid, fromId)
                    .last(GenConstants.LIMIT_1));
            chatData.put(key, JSON.parse(JSON.toJSONString(weCustomer)));
        } else if (WeConstans.ID_TYPE_MACHINE.equals(fromType)) {
            //拉去机器人信息暂不处理
        }
    }


    /**
     * 发送人转接收人
     *
     * @param fromType
     * @param fromWeChatContactMapping
     * @return
     */
    private WeChatContactMapping from2ReveiceData(int fromType, WeChatContactMapping fromWeChatContactMapping) {
        //接收人映射数据
        WeChatContactMapping reveiceWeChatContactMapping = new WeChatContactMapping();
        if (StringUtils.isNotEmpty(fromWeChatContactMapping.getRoomId())) {
            reveiceWeChatContactMapping.setFromId(fromWeChatContactMapping.getRoomId());
        } else {
            reveiceWeChatContactMapping.setFromId(fromWeChatContactMapping.getReceiveId());
        }
        reveiceWeChatContactMapping.setReceiveId(fromWeChatContactMapping.getFromId());
        reveiceWeChatContactMapping.setIsCustom(fromType);
        reveiceWeChatContactMapping.setCorpId(fromWeChatContactMapping.getCorpId());
        return reveiceWeChatContactMapping;
    }
}
