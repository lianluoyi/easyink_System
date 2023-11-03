package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.chat.ChatTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeChatContactMapping;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.dto.WeChatMappingDTO;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.wecom.mapper.WeChatContactMappingMapper;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.WeChatContactMappingService;
import com.easyink.wecom.service.WeConversationArchiveService;
import com.easyink.wecom.service.WeGroupMemberService;
import com.easyink.wecom.service.WeGroupService;
import com.easyink.wecom.service.idmapping.WeExternalUserIdMappingService;
import com.easyink.wecom.service.idmapping.WeUserIdMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    @Autowired
    private WeUserIdMappingService weUserIdMappingService;
    @Autowired
    private RuoYiConfig ruoYiConfig ;
    @Autowired
    private WeExternalUserIdMappingService weExternalUserIdMappingService;

    private final WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;

    @Autowired
    public WeChatContactMappingServiceImpl(WeFlowerCustomerRelMapper weFlowerCustomerRelMapper) {
        this.weFlowerCustomerRelMapper = weFlowerCustomerRelMapper;
    }

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
                    item.setFinalChatContext(weConversationArchiveService.getFinalChatRoomContactInfo(weChatContactMapping.getFromId(), item.getRoomId(), weChatContactMapping.getCorpId()));
                }
            }
        }
        Collections.sort(weChatMappingList);
        return weChatMappingList;
    }

    /**
     * 查询聊天关系映射列表
     *
     * @param weChatMappingDTO {@link WeChatMappingDTO}
     * @return 聊天关系映射
     */
    @DataScope
    @Override
    public List<WeChatContactMapping> selectWeChatContactMappingListV2(WeChatMappingDTO weChatMappingDTO) {
        if (ObjectUtils.isEmpty(weChatMappingDTO) || StringUtils.isEmpty(weChatMappingDTO.getCorpId()) || PageInfoUtil.getPageNum() == null || PageInfoUtil.getPageSize() == null) {
            return new ArrayList<>();
        }
        // 不查询总数分页
        PageInfoUtil.startPageNoCount();
        // 查询关系映射
        List<WeChatContactMapping> weChatMappingList = weChatContactMappingMapper.selectWeChatContactMappingListV2(weChatMappingDTO);
        if (CollectionUtils.isEmpty(weChatMappingList)) {
            return weChatMappingList;
        }
        // 内部联系人查询
        if (ChatTypeEnum.INSIDE_CHAT.getType().equals(weChatMappingDTO.getSearchType())) {
            // 需要查询信息的员工id列表
            List<String> userIdList = weChatMappingList.stream().filter(item -> WeConstans.ID_TYPE_USER.equals(item.getIsCustom())).map(WeChatContactMapping::getReceiveId).collect(Collectors.toList());
            // 根据id列表查询并设置员工信息
            searchAndSetUserInfo(weChatMappingDTO.getCorpId(), userIdList, weChatMappingList);
        } else if (ChatTypeEnum.OUTSIDE_CHAT.getType().equals(weChatMappingDTO.getSearchType())) {
            // 外部联系人查询
            // 需要查询信息的客户id列表
            List<String> externalUseridList = weChatMappingList.stream().filter(item -> WeConstans.ID_TYPE_EX.equals(item.getIsCustom())).map(WeChatContactMapping::getReceiveId).collect(Collectors.toList());
            // 根据id列表查询并设置客户信息
            searchAndSetCustomerInfo(weChatMappingDTO.getCorpId(), externalUseridList, weChatMappingList);
        } else {
            // 群聊查询
            // 需要查询信息的群id列表
            List<String> roomIdList = weChatMappingList.stream().map(WeChatContactMapping::getRoomId).collect(Collectors.toList());
            // 根据id列表查询并设置群信息
            searchAndSetGroupInfo(weChatMappingDTO.getCorpId(), roomIdList, weChatMappingList );
        }
        Collections.sort(weChatMappingList);
        return weChatMappingList;
    }

    /**
     * 根据id列表设置群和群成员信息
     *
     * @param corpId            企业ID
     * @param roomIdList        群id列表
     * @param weChatMappingList {@link List<WeChatContactMapping>}
     */
    private void searchAndSetGroupInfo(String corpId, List<String> roomIdList, List<WeChatContactMapping> weChatMappingList) {
        // 群信息列表
        List<WeGroup> groupList = weGroupService.list(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getCorpId, corpId).in(WeGroup::getChatId, roomIdList));
        if (CollectionUtils.isEmpty(groupList)) {
            return;
        }
        // 查询群成员头像信息
        List<WeGroupMemberDTO> weGroupMemberDtoList = weGroupMemberService.selectWeGroupMemberListByChatIdList(roomIdList);
        // 不为空，设置群聊下的群成员信息
        if (CollectionUtils.isNotEmpty(weGroupMemberDtoList)) {
            Map<String, List<WeGroupMemberDTO>> chatMemberMap = weGroupMemberDtoList.stream().collect(Collectors.groupingBy(WeGroupMemberDTO::getChatId));
            // 为每个群聊设置群成员信息
            for (WeGroup weGroup : groupList) {
                if (chatMemberMap.get(weGroup.getChatId()) == null) {
                    continue;
                }
                // 最多九个，用,隔开
                String roomAvatar = chatMemberMap.get(weGroup.getChatId()).stream()
                        .map(WeGroupMemberDTO::getMemberAvatar)
                        .filter(StringUtils::isNotEmpty)
                        .limit(9)
                        .collect(Collectors.joining(","));
                weGroup.setAvatar(roomAvatar);
            }
            // 设置群聊信息
            for (WeChatContactMapping weChatContactMapping : weChatMappingList) {
                for (WeGroup weGroup : groupList) {
                    if (weChatContactMapping.getRoomId().equals(weGroup.getChatId())) {
                        weChatContactMapping.setRoomInfo(weGroup);
                    }
                }
                if (org.apache.commons.lang3.StringUtils.isNoneBlank(weChatContactMapping.getFromId(), weChatContactMapping.getRoomId())) {
                    weChatContactMapping.setFinalChatContext(weConversationArchiveService.getFinalChatRoomContactInfo(weChatContactMapping.getFromId(), weChatContactMapping.getRoomId(), weChatContactMapping.getCorpId()));
                }
            }
        }
    }

    /**
     * 根据id列表查询并设置客户信息
     *
     * @param corpId             企业ID
     * @param externalUseridList 客户ID列表
     * @param weChatMappingList  {@link List<WeChatContactMapping>}
     */
    private void searchAndSetCustomerInfo(String corpId, List<String> externalUseridList, List<WeChatContactMapping> weChatMappingList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(externalUseridList) || CollectionUtils.isEmpty(weChatMappingList)) {
            return;
        }
        // 客户信息列表
        List<WeCustomer> weCustomerList = weCustomerMapper.selectList(new LambdaQueryWrapper<WeCustomer>()
                .eq(WeCustomer::getCorpId, corpId)
                .in(WeCustomer::getExternalUserid, externalUseridList));
        // 不为空，就补充客户信息
        if (CollectionUtils.isNotEmpty(weCustomerList)) {
            // 赋值客户信息
            for (WeChatContactMapping chatContactMapping : weChatMappingList) {
                // 为对应客户设置信息
                for (WeCustomer weCustomer : weCustomerList) {
                    if (chatContactMapping.getReceiveId().equals(weCustomer.getExternalUserid())) {
                        chatContactMapping.setReceiveWeCustomer(weCustomer);
                    }
                }
                // 为聊天查询最近一条消息记录
                if (StringUtils.isNotBlank(chatContactMapping.getFromId()) && StringUtils.isNotBlank(chatContactMapping.getReceiveId())) {
                    chatContactMapping.setFinalChatContext(weConversationArchiveService.getFinalChatContactInfo(chatContactMapping.getFromId(), chatContactMapping.getReceiveId(), chatContactMapping.getCorpId()));
                }
            }
        }
    }

    /**
     * 根据id列表查询并设置员工信息
     *
     * @param corpId            企业ID
     * @param userIdList        员工ID列表
     * @param weChatMappingList {@link List<WeChatContactMapping>}
     */
    private void searchAndSetUserInfo(String corpId, List<String> userIdList, List<WeChatContactMapping> weChatMappingList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(userIdList) || CollectionUtils.isEmpty(weChatMappingList)) {
            return;
        }
        // 员工信息列表
        List<WeUser> weUserList = weUserMapper.selectList(new LambdaQueryWrapper<WeUser>()
                .eq(WeUser::getCorpId, corpId)
                .in(WeUser::getUserId, userIdList));
        // 不为空，就补充员工信息
        if (CollectionUtils.isNotEmpty(weUserList)) {
            // 赋值员工信息
            for (WeChatContactMapping chatContactMapping : weChatMappingList) {
                // 为员工设置信息
                for (WeUser weUser : weUserList) {
                    if (chatContactMapping.getReceiveId().equals(weUser.getUserId())) {
                        chatContactMapping.setReceiveWeUser(weUser);
                    }
                }
                // 为聊天查询最近一条消息记录
                if (StringUtils.isNotBlank(chatContactMapping.getFromId()) && StringUtils.isNotBlank(chatContactMapping.getReceiveId())) {
                    chatContactMapping.setFinalChatContext(weConversationArchiveService.getFinalChatContactInfo(chatContactMapping.getFromId(), chatContactMapping.getReceiveId(), chatContactMapping.getCorpId()));
                }
            }
        }
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
            WeChatContactMapping contactMapping = new WeChatContactMapping();
            contactMapping.setCorpId(corpId);
            String fromId = chatData.getFrom();
            String toId = String.valueOf(chatData.getTolist().get(0));
            //发送人类型
            int fromType = StringUtils.weCustomTypeJudgment(fromId);
            getUserOrCustomerInfo1(chatData, fromId, fromType, WeConstans.FROMM_INFO, corpId, contactMapping);
            List<String> tolist = chatData.getTolist();
            if (CollUtil.isNotEmpty(tolist)) {
                //如果是单聊，tolist唯一，只有一个接收人，并判断接收人的类型
                if (StringUtils.isEmpty(chatData.getRoomid())) {
                    //接收人类型
                    int reveiceType = StringUtils.weCustomTypeJudgment(toId);
                    //获取接收人信息
                    getUserOrCustomerInfo1(chatData, toId, reveiceType, WeConstans.TO_LIST_INFO, corpId, contactMapping);
                    contactMapping.setIsCustom(reveiceType);
                } else {
                    tranferGroupMemberIdForDK(corpId, tolist);
                    contactMapping.setRoomId(chatData.getRoomid());
                    WeGroup weGroup = weGroupService.getOne(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getChatId, chatData.getRoomid())
                                                                                             .last("limit 1"));
                    chatData.setRoomInfo(JSON.parse(JSON.toJSONString(weGroup)));
                }
            }
            //接收人映射数据
            WeChatContactMapping reveiceWeChatContactMapping = from2ReveiceData(fromType, contactMapping);
            insertWeChatContactMapping(contactMapping);
            insertWeChatContactMapping(reveiceWeChatContactMapping);
            resultList.add(chatData);
        });
        return resultList;
    }

    /**
     * 代开发应用转换群聊 客户/成员id
     *
     * @param corpId 企业id
     * @param tolist 接收人id列表
     */
    private void tranferGroupMemberIdForDK(String corpId, List<String> tolist) {
        if (ruoYiConfig.isInternalServer()) {
            return;
        }
        for (String toId : tolist) {
            // 查询是否是员工或者客户
            int toType = StringUtils.weCustomTypeJudgment(toId);
            if (WeConstans.ID_TYPE_USER.equals(toType)) {
                //成员信息
                WeUser weUser = weUserMapper.selectOne(new LambdaQueryWrapper<WeUser>()
                        .eq(WeUser::getCorpId, corpId)
                        .eq(WeUser::getUserId, weUserIdMappingService.getOpenUserIdByUserId(corpId, toId)));
                log.info("[会话存档-群聊]获取员工信息,corpId:{}, user:{},toId:{}", corpId, weUser, toId);
                if (weUser == null) {
                    return;
                }
                toId = weUser.getUserId();
            } else if (WeConstans.ID_TYPE_EX.equals(toType)) {
                // 获取外部联系人信息
                WeCustomer weCustomer = weCustomerMapper.selectOne(new LambdaQueryWrapper<WeCustomer>()
                        .eq(WeCustomer::getCorpId, corpId)
                        .eq(WeCustomer::getExternalUserid, weExternalUserIdMappingService.getOpenExternalUserIdByExternalUserId(corpId, toId))
                        .last(GenConstants.LIMIT_1));
                log.info("[会话存档-群聊]获取客户信息,corpId:{}, customer:{},toId:{}", corpId, weCustomer, toId);
                if (weCustomer == null) {
                    return;
                }
                toId = weCustomer.getExternalUserid();
            }
        }
    }

    private void getUserOrCustomerInfo1(ChatInfoVO chatData, String fromId, int fromType, String key, String corpId, WeChatContactMapping contactMapping) {
        if (StringUtils.isEmpty(fromId) || StringUtils.isEmpty(key) || StringUtils.isEmpty(corpId)) {
            return;
        }
        //获取发送人信息
        if (WeConstans.ID_TYPE_USER.equals(fromType)) {
            //成员信息
            WeUser weUser = weUserMapper.selectOne(new LambdaQueryWrapper<WeUser>()
                    .eq(WeUser::getCorpId, corpId)
                    .eq(WeUser::getUserId, weUserIdMappingService.getOpenUserIdByUserId(corpId, fromId)));
            log.info("[会话存档]获取员工信息,corpId:{}, user:{},fromId:{}", corpId, weUser, fromId);
            if (weUser == null) {
                return;
            }

            if (key.equals(WeConstans.TO_LIST_INFO)) {
                chatData.setToListInfo(JSON.parse(JSON.toJSONString(weUser)));
                List<String> toList = new ArrayList<>();
                toList.add(weUser.getUserId()) ;
                chatData.setTolist(toList);
                contactMapping.setReceiveId(weUser.getUserId());
            } else if (key.equals(WeConstans.FROMM_INFO)) {
                chatData.setFromInfo(JSON.parse(JSON.toJSONString(weUser)));
                contactMapping.setFromId(weUser.getUserId());
                chatData.setFrom(weUser.getUserId());
            }
        } else if (WeConstans.ID_TYPE_EX.equals(fromType)) {
            // 获取外部联系人信息
            WeCustomer weCustomer = weCustomerMapper.selectOne(new LambdaQueryWrapper<WeCustomer>()
                    .eq(WeCustomer::getCorpId, corpId)
                    .eq(WeCustomer::getExternalUserid, weExternalUserIdMappingService.getOpenExternalUserIdByExternalUserId(corpId, fromId))
                    .last(GenConstants.LIMIT_1));
            log.info("[会话存档]获取客户信息,corpId:{}, customer:{},fromId:{}", corpId, weCustomer, fromId);
            if (weCustomer == null) {
                return;
            }

            // 员工Id
            String userId;
            // 如果客户是接收消息者，员工为发送消息者
            if (key.equals(WeConstans.TO_LIST_INFO)) {
                userId = chatData.getFrom();
                // 根据WeFlowerCustomerRel表，将员工-客户建立好友时间更新
                updateCustomerRelTime(userId, corpId, fromId, weCustomer);
                contactMapping.setReceiveId(weCustomer.getExternalUserid());
                List<String> receiverList = new ArrayList<>();
                receiverList.add(weCustomer.getExternalUserid());
                chatData.setTolist(receiverList);
                // 获取员工客户映射关系信息
                chatData.setToListInfo(JSON.parse(JSON.toJSONString(weCustomer)));
            } else if (key.equals(WeConstans.FROMM_INFO)) {
                // 客户是发送消息者，员工为接收消息者。
                userId = chatData.getTolist().get(0);
                // 根据WeFlowerCustomerRel表，将员工-客户建立好友时间更新
                updateCustomerRelTime(userId, corpId, fromId, weCustomer);
                chatData.setFromInfo(JSON.parse(JSON.toJSONString(weCustomer)));
                chatData.setFrom(weCustomer.getExternalUserid());
                contactMapping.setFromId(weCustomer.getExternalUserid());
            }
        } else if (WeConstans.ID_TYPE_MACHINE.equals(fromType)) {
            //拉去机器人信息暂不处理
        }
    }

    /**
     * 获取正确的员工-客户建立好友关系时间
     *
     * @param userId 员工ID
     * @param corpId 企业ID
     * @param externalUserId 客户ID
     * @param weCustomer {@link WeCustomer}
     */
    public void updateCustomerRelTime(String userId, String corpId, String externalUserId, WeCustomer weCustomer){
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(corpId) || StringUtils.isEmpty(externalUserId) || weCustomer == null){
            // TODO 兼容待开发应用 为了线上验收所加 ,后续可删除
            log.warn("[param missing] [updateCustomerRelTime] userId:{}, corpId:{}, externalUserid :{} , customer :{}", userId, corpId, externalUserId, weCustomer);
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 获取员工-客户关系信息
        WeFlowerCustomerRel weFlowerCustomerRel = weFlowerCustomerRelMapper.selectOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .eq(WeFlowerCustomerRel::getUserId, userId)
                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserId)
                .last(GenConstants.LIMIT_1));
        if (weFlowerCustomerRel != null) {
            // 更新时间
            weCustomer.setCreateTime(weFlowerCustomerRel.getCreateTime());
            weCustomer.setUpdateTime(weFlowerCustomerRel.getDeleteTime());
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
