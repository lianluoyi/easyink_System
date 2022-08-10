package com.easywecom.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.annotation.DataScope;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.CustomerTrajectoryEnums;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.BaseException;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.WeCustomerGroupClient;
import com.easywecom.wecom.domain.WeCustomerAddGroup;
import com.easywecom.wecom.domain.WeGroup;
import com.easywecom.wecom.domain.WeGroupMember;
import com.easywecom.wecom.domain.dto.FindWeGroupDTO;
import com.easywecom.wecom.domain.dto.WeGroupMemberDTO;
import com.easywecom.wecom.domain.dto.customer.CustomerGroupDetail;
import com.easywecom.wecom.domain.dto.customer.CustomerGroupList;
import com.easywecom.wecom.domain.dto.customer.CustomerGroupMember;
import com.easywecom.wecom.domain.vo.sop.GroupSopVO;
import com.easywecom.wecom.domain.vo.wegrouptag.WeGroupTagRelDetail;
import com.easywecom.wecom.domain.vo.wegrouptag.WeGroupTagRelVO;
import com.easywecom.wecom.mapper.WeGroupMapper;
import com.easywecom.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

import static com.easywecom.common.utils.DateUtils.YYYY_MM_DD;

/**
 * 类名： WeGroupServiceImpl
 *
 * @author 佚名
 * @date 2021/8/31 14:28
 */
@Slf4j
@Service
@Validated
public class WeGroupServiceImpl extends ServiceImpl<WeGroupMapper, WeGroup> implements WeGroupService {
    private final WeCustomerGroupClient weCustomerGroupClient;
    private final WeGroupMemberService weGroupMemberService;
    private final PageHomeService pageHomeService;
    private final WeGroupTagRelService weGroupTagRelService;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;

    @Autowired
    @Lazy
    public WeGroupServiceImpl(WeCustomerGroupClient weCustomerGroupClient, WeGroupMemberService weGroupMemberService, PageHomeService pageHomeService, WeGroupTagRelService weGroupTagRelService, WeCustomerTrajectoryService weCustomerTrajectoryService) {
        this.weCustomerGroupClient = weCustomerGroupClient;
        this.weGroupMemberService = weGroupMemberService;
        this.pageHomeService = pageHomeService;
        this.weGroupTagRelService = weGroupTagRelService;
        this.weCustomerTrajectoryService = weCustomerTrajectoryService;
    }

    private static final int LENGTH = 3;
    private static final long TIME_TERM = 1000L;

    @Override
    public List<GroupSopVO> listOfChat(String chatName, List<String> chatIds) {
        if (CollectionUtils.isEmpty(chatIds)) {
            return new ArrayList<>();
        }
        return baseMapper.listOfChat(chatName, chatIds);
    }

    @Override
    @DataScope
    public List<WeGroup> selectWeGroupList(WeGroup weGroup) {
        if (org.apache.commons.lang3.StringUtils.isBlank(weGroup.getCorpId())) {
            log.error("企业id不能为空");
            throw new BaseException("查询群聊列表报错");
        }
        return this.baseMapper.selectWeGroupList(weGroup);
    }

    @Override
    public List<String> listOfOwnerId(String corpId, String[] departments) {
        return this.baseMapper.listOfOwnerId(corpId, departments);
    }

    @Override
    @DataScope
    public List<WeGroup> list(FindWeGroupDTO weGroupDTO) {
        if (weGroupDTO == null || org.apache.commons.lang3.StringUtils.isBlank(weGroupDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //格式化参数时间为yyyyMMdd hh:mm:ss
        if (StringUtils.isNotEmpty(weGroupDTO.getEndTime())) {
            if (DateUtils.isMatchFormat(weGroupDTO.getBeginTime(), YYYY_MM_DD)) {
                String endDay = DateUtils.parseEndDay(weGroupDTO.getEndTime());
                weGroupDTO.setEndTime(endDay);
            }
        }
        if (StringUtils.isNotEmpty(weGroupDTO.getBeginTime())) {
            if (DateUtils.isMatchFormat(weGroupDTO.getBeginTime(), YYYY_MM_DD)) {
                String beginDay = DateUtils.parseBeginDay(weGroupDTO.getBeginTime());
                weGroupDTO.setBeginTime(beginDay);
            }
        }
        List<WeGroup> weGroupList = baseMapper.list(weGroupDTO);
        if (CollectionUtils.isEmpty(weGroupList)) {
            return new ArrayList<>();
        }
        //查询这些群的群标签关系
        List<String> chatIdList = weGroupList.stream().map(WeGroup::getChatId).collect(Collectors.toList());
        List<WeGroupTagRelVO> relList = weGroupTagRelService.getByChatIdList(weGroupDTO.getCorpId(), chatIdList);

        //chatId,List<WeGroupTagRelDetail>
        Map<String, List<WeGroupTagRelDetail>> tagDataMap = new HashMap<>();
        for (WeGroupTagRelVO relVO : relList) {
            tagDataMap.put(relVO.getChatId(), relVO.getTagList());
        }
        //组装标签数据
        List<WeGroupTagRelDetail> tagList;
        List<WeGroupTagRelDetail> newList = new ArrayList<>();
        for (WeGroup group : weGroupList) {
            tagList = tagDataMap.get(group.getChatId());
            if (tagList != null) {
                group.setTagList(tagList);
            } else {
                group.setTagList(newList);
            }
        }
        return weGroupList;
    }


    /**
     * @param corpId 企业id，不能为空
     * @param params 含有客户群跟进状态过滤。0 - 所有列表(即不过滤) 1 - 离职待继承 2 - 离职继承中 3 - 离职继承完成
     * @return
     */
    @Override
    public List<CustomerGroupList.GroupChat> getGroupChats(@NotBlank(message = "企业id不能为空") String corpId, CustomerGroupList.Params params) {

        List<CustomerGroupList.GroupChat> list = new ArrayList<>();
        //用于do while循环
        CustomerGroupList customerGroupList = new CustomerGroupList();
        do {
            //如果不是第一次获取企微数据
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(customerGroupList.getNext_cursor())) {
                params.setCursor(customerGroupList.getNext_cursor());
            }
            customerGroupList = weCustomerGroupClient.groupChatLists(params, corpId);
            if (CollUtil.isNotEmpty(customerGroupList.getGroup_chat_list())) {
                list.addAll(customerGroupList.getGroup_chat_list());
            }
        } while (org.apache.commons.lang3.StringUtils.isNotBlank(customerGroupList.getNext_cursor()));
        return list;
    }

    /**
     * 同步客户群
     *
     * @param corpId 企业id
     */
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void syncWeGroup(String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            log.error("企业id不能不空");
            throw new BaseException("客户群同步失败");
        }
        //调用企微新接口
        CustomerGroupList customerGroupList = weCustomerGroupClient.groupChatLists(new CustomerGroupList().new Params(), corpId);
        if (WeConstans.WE_SUCCESS_CODE.equals(customerGroupList.getErrcode())
                && CollUtil.isNotEmpty(customerGroupList.getGroup_chat_list())) {
            List<String> charIds = customerGroupList.getGroup_chat_list().stream().map(CustomerGroupList.GroupChat::getChat_id).collect(Collectors.toList());
            //删除所有不存在的数据
            if (CollUtil.isEmpty(charIds)) {
                //删除所有群聊数据
                this.remove(new LambdaQueryWrapper<WeGroup>()
                        .isNotNull(WeGroup::getChatId)
                        .eq(WeGroup::getCorpId, corpId));
                //删除所有群聊成员数据
                weGroupMemberService.remove(new LambdaQueryWrapper<WeGroupMember>()
                        .isNotNull(WeGroupMember::getChatId)
                        .eq(WeGroupMember::getChatId, corpId));
                return;
            }
            //删除不存在的群组数据
            int weGroupNotInCount = this.count(new LambdaQueryWrapper<WeGroup>()
                    .notIn(WeGroup::getChatId, charIds)
                    .eq(WeGroup::getCorpId, corpId));
            if (weGroupNotInCount > 0) {
                this.remove(new LambdaQueryWrapper<WeGroup>()
                        .notIn(WeGroup::getChatId, charIds)
                        .eq(WeGroup::getCorpId, corpId));
            }
            //删除所有群聊列表数据
            int count = weGroupMemberService.count(new LambdaQueryWrapper<WeGroupMember>().eq(WeGroupMember::getCorpId, corpId));
            if (count > 0) {
                weGroupMemberService.remove(new LambdaQueryWrapper<WeGroupMember>()
                        .eq(WeGroupMember::getCorpId, corpId)
                        .isNotNull(WeGroupMember::getChatId));
            }

            List<WeGroup> weGroups = new ArrayList<>();
            List<WeGroupMember> weGroupMembers = new ArrayList<>();

            for (CustomerGroupList.GroupChat chat : customerGroupList.getGroup_chat_list()) {
                if (org.apache.commons.lang3.StringUtils.isBlank(chat.getChat_id())) {
                    //跳过本次循环
                    continue;
                }
                //根据chatId获取客户群详情
                CustomerGroupDetail customerGroupDetail = weCustomerGroupClient.groupChatDetail(
                        new CustomerGroupDetail().new Params(chat.getChat_id(), WeConstans.NEED_NAME), corpId
                );

                //如果获取成功且不为空则补充群聊数据加入到list集合中
                if (customerGroupDetail.getErrcode().equals(WeConstans.WE_SUCCESS_CODE) && CollUtil.isNotEmpty(customerGroupDetail.getGroup_chat())) {
                    customerGroupDetail.getGroup_chat().forEach(groupChat -> {
                        //获取群聊列表，将数据补充加入到list集合
                        List<CustomerGroupMember> memberLists = groupChat.getMember_list();
                        //群名若为空设置备用群名
                        if (StringUtils.isEmpty(groupChat.getName())) {
                            groupChat.setName(getExtraChatName(memberLists));
                        }
                        if (CollUtil.isNotEmpty(memberLists)) {
                            for (CustomerGroupMember member : memberLists) {
                                String invitorUserId = member.getInvitor() != null && member.getInvitor().getUserid() != null ? member.getInvitor().getUserid() : "";

                                //由于群聊列表已删除，则id由雪花算法生成一个
                                weGroupMembers.add(
                                        WeGroupMember.builder()
                                                .id(SnowFlakeUtil.nextId())
                                                .chatId(groupChat.getChat_id())
                                                .userId(member.getUserid())
                                                .corpId(corpId)
                                                .joinTime(DateUtil.date(member.getJoin_time() * TIME_TERM))
                                                .joinScene(member.getJoin_scene())
                                                .joinType(member.getType())
                                                .unionId(member.getUnionid())
                                                .memberName(member.getName())
                                                .invitor(invitorUserId)
                                                .build()
                                );
                            }
                        }
                        //群数据列表添加
                        weGroups.add(
                                WeGroup.builder()
                                        .chatId(groupChat.getChat_id())
                                        .groupName(groupChat.getName())
                                        .corpId(corpId)
                                        .notice(groupChat.getNotice())
                                        .owner(groupChat.getOwner())
                                        .createTime(DateUtil.date(groupChat.getCreate_time() * TIME_TERM))
                                        .status(chat.getStatus())
                                        .build()
                        );
                    });
                }
            }


            /**
             * 此处不同步成员和客户理由
             * 1、如果是存量同步，不存在新客户新成员问题
             * 2、增量同步的，成员和客户增删改群变更都会走回调通知，不用考虑新增成员和客户回调通知没有处理问题
             */

            if (CollUtil.isNotEmpty(weGroups)) {
                this.saveOrUpdateBatch(weGroups);
            }
            if (CollUtil.isNotEmpty(weGroupMembers)) {
                weGroupMemberService.saveBatch(weGroupMembers);
            }
        }
        //同步客户群后刷新数据概览页数据
        pageHomeService.getGroupData(corpId);
    }

    /**
     * 获取备用群名
     *
     * @param memberLists 群成员
     * @return 备用群名
     */
    private String getExtraChatName(List<CustomerGroupMember> memberLists) {
        StringBuilder chatName = new StringBuilder();
        int customerNum = 0;
        //备用群名拼接
        for (CustomerGroupMember member : memberLists) {
            //备用群名群成员添加
            if (customerNum < LENGTH) {
                chatName.append(member.getName()).append(WeConstans.COMMA);
                customerNum++;
            } else {
                break;
            }
        }
        chatName.append(WeConstans.SUFFIX);
        //删除多余的逗号
        int index = chatName.lastIndexOf(WeConstans.COMMA);
        if (index > 0) {
            chatName.deleteCharAt(index);
        }
        return chatName.toString();
    }

    /**
     * 创建群聊
     *
     * @param chatId 客户群id
     * @param corpId 企业id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createWeGroup(String chatId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(corpId, chatId)) {
            log.error("群聊id和企微id都不能为空：corpId：{}，charId：{}", corpId, chatId);
            throw new BaseException("创建群聊失败，公司id和群聊id不能为空");
        }

        List<WeGroup> weGroups = new ArrayList<>();
        List<WeGroupMember> weGroupMembers = new ArrayList<>();

        //使用企微新接口
        CustomerGroupDetail customerGroupDetail = weCustomerGroupClient.groupChatDetail(
                new CustomerGroupDetail().new Params(chatId, WeConstans.NEED_NAME), corpId
        );
        if (customerGroupDetail.isSuccess() && CollUtil.isNotEmpty(customerGroupDetail.getGroup_chat())) {
            for (CustomerGroupDetail.GroupChat chat : customerGroupDetail.getGroup_chat()) {
                List<CustomerGroupMember> memberLists = chat.getMember_list();
                if (CollUtil.isEmpty(memberLists)) {
                    //群聊列表不能为空
                    continue;
                }
                //群名若为空设置备用群名
                if (StringUtils.isEmpty(chat.getName())) {
                    chat.setName(getExtraChatName(memberLists));
                }
                weGroups.add(
                        WeGroup.builder()
                                .chatId(chat.getChat_id())
                                .groupName(chat.getName())
                                .corpId(corpId)
                                .notice(chat.getNotice())
                                .owner(chat.getOwner())
                                .createTime(new Date(chat.getCreate_time() * TIME_TERM))
                                .build()
                );
                if (CollUtil.isNotEmpty(memberLists)) {
                    for (CustomerGroupMember member : memberLists) {
                        String invitorUserId = member.getInvitor() != null && member.getInvitor().getUserid() != null ? member.getInvitor().getUserid() : "";
                        weGroupMembers.add(
                                WeGroupMember.builder()
                                        .chatId(chat.getChat_id())
                                        .userId(member.getUserid())
                                        .corpId(corpId)
                                        .joinTime(new Date(member.getJoin_time() * TIME_TERM))
                                        .joinScene(member.getJoin_scene())
                                        .joinType(member.getType())
                                        .memberName(member.getName())
                                        .unionId(member.getUnionid())
                                        .invitor(invitorUserId)
                                        .build()
                        );
                    }
                }
            }


            if (CollUtil.isNotEmpty(weGroups)) {
                this.saveOrUpdateBatch(weGroups);
            }
            if (CollUtil.isNotEmpty(weGroupMembers)) {
                weGroupMemberService.saveBatch(weGroupMembers);
                // 客户轨迹记录: 客户加入群聊
                weCustomerTrajectoryService.saveActivityRecord(weGroupMembers, CustomerTrajectoryEnums.SubType.JOIN_GROUP.getType());
            }
        }
    }

    /**
     * 修改群聊
     *
     * @param corpId 公司id
     * @param chatId 群聊id
     * @return 变动的群成员列表信息, 未过滤
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WeGroupMember> updateWeGroup(String corpId, String chatId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(corpId, chatId)) {
            log.error("群聊id和企微id都不能为空：corpId：{}，charId：{}", corpId, chatId);
            throw new BaseException("修改群聊失败，公司id和群聊id不能为空");
        }
        List<WeGroup> weGroups = new ArrayList<>();
        List<WeGroupMember> weGroupMembers = new ArrayList<>();

        //企微新接口
        CustomerGroupDetail customerGroupDetail = weCustomerGroupClient.groupChatDetail(
                new CustomerGroupDetail().new Params(chatId, WeConstans.NEED_NAME), corpId
        );
        //获取表中成员信息,chatId唯一，不需要判断公司id
        List<WeGroupMember> localMemberList = weGroupMemberService.list(new LambdaQueryWrapper<WeGroupMember>()
                .eq(WeGroupMember::getChatId, chatId));
        if (CollUtil.isNotEmpty(customerGroupDetail.getGroup_chat())) {
            for (CustomerGroupDetail.GroupChat weGroup : customerGroupDetail.getGroup_chat()) {
                List<CustomerGroupMember> memberLists = weGroup.getMember_list();
                if (CollUtil.isEmpty(memberLists)) {
                    continue;
                }
                //群名若为空设置备用群名
                if (StringUtils.isEmpty(weGroup.getName())) {
                    weGroup.setName(getExtraChatName(memberLists));
                }
                //构造成员列表
                for (CustomerGroupMember customerGroupMember : memberLists) {
                    String invitorUserId = customerGroupMember.getInvitor() != null && customerGroupMember.getInvitor().getUserid() != null ? customerGroupMember.getInvitor().getUserid() : "";
                    weGroupMembers.add(
                            WeGroupMember.builder()
                                    .chatId(weGroup.getChat_id())
                                    .userId(customerGroupMember.getUserid())
                                    .joinTime(new Date(customerGroupMember.getJoin_time() * TIME_TERM))
                                    .joinScene(customerGroupMember.getJoin_scene())
                                    .corpId(corpId)
                                    .joinType(customerGroupMember.getType())
                                    .unionId(customerGroupMember.getUnionid())
                                    .memberName(customerGroupMember.getName())
                                    .invitor(invitorUserId)
                                    .build()
                    );
                }
                //添加群进列表
                weGroups.add(
                        WeGroup.builder()
                                .chatId(weGroup.getChat_id())
                                .groupName(weGroup.getName())
                                .notice(weGroup.getNotice())
                                .owner(weGroup.getOwner())
                                .corpId(corpId)
                                .createTime(new Date(weGroup.getCreate_time() * TIME_TERM))
                                .build()
                );
            }

            if (CollUtil.isNotEmpty(weGroups)) {
                this.saveOrUpdateBatch(weGroups);
            }

            List<WeGroupMember> list = new ArrayList<>();
            if (weGroupMembers.size() > localMemberList.size()) {
                //成员信息取差集
                list = weGroupMembers.stream().filter(m -> !localMemberList.stream()
                        .map(WeGroupMember::getUserId).collect(Collectors.toList()).contains(m.getUserId()))
                        .collect(Collectors.toList());
                weGroupMemberService.batchInsert(list);
                // 客户轨迹记录: 客户加入群聊
                weCustomerTrajectoryService.saveActivityRecord(list, CustomerTrajectoryEnums.SubType.JOIN_GROUP.getType());
            } else if (weGroupMembers.size() < localMemberList.size()) {
                //成员信息取差集
                list = localMemberList.stream().filter(m -> !weGroupMembers.stream()
                        .map(WeGroupMember::getUserId).collect(Collectors.toList()).contains(m.getUserId()))
                        .collect(Collectors.toList());
                weGroupMemberService.remove(new LambdaQueryWrapper<WeGroupMember>()
                        .eq(WeGroupMember::getChatId, chatId)
                        .in(WeGroupMember::getUserId, list.stream().map(WeGroupMember::getUserId)
                                .collect(Collectors.toList())));
                // 客户轨迹记录: 客户退出群聊
                weCustomerTrajectoryService.saveActivityRecord(list, CustomerTrajectoryEnums.SubType.QUIT_GROUP.getType());
            }
            // 这里返回的list也是表示变动群成员列表信息
            return list;
        }
        // 调用企业微信群详情接口为空,则表示只剩下群主,localMemberList则表示退群变动的成员列表信息
        return localMemberList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWeGroup(String chatId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(chatId, corpId)) {
            log.error("删除群聊失败，chatId：{}，corpId:{}", chatId, corpId);
            throw new BaseException("删除群聊失败");
        }
        this.baseMapper.delete(new LambdaQueryWrapper<WeGroup>()
                .eq(WeGroup::getCorpId, corpId)
                .eq(WeGroup::getChatId, chatId));
        weGroupMemberService.remove(new LambdaQueryWrapper<WeGroupMember>()
                .eq(WeGroupMember::getCorpId, corpId)
                .eq(WeGroupMember::getChatId, chatId));
    }

    @Override
    public List<WeCustomerAddGroup> findWeGroupByCustomer(String userId, String externalUserid, String corpId) {
        return this.baseMapper.findWeGroupByCustomer(userId, externalUserid, corpId);
    }

    @Override
    public CustomerGroupDetail selectWeGroupDetail(String chatId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(chatId, corpId)) {
            log.error("群聊id和公司id不能为空：charId：{}，corpId：{}", chatId, corpId);
            throw new BaseException("根据群聊id获取群聊详情");
        }
        return weCustomerGroupClient.groupChatDetail(new CustomerGroupDetail().new Params(chatId), corpId);
    }

    /**
     * 根据userId获取群聊列表，会话存档模块内部调用
     *
     * @param userId 员工id
     * @param corpId 企业id
     * @return 客户群列表
     */
    @Override
    public List<WeGroup> selectWeGroupListByUserid(String userId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(corpId, userId)) {
            log.error("客户id和企微id都不能为空：corpId：{}，userId：{}", corpId, userId);
            throw new BaseException("根据userId获取群聊列表，userid和群聊id不能为空");
        }
        List<WeGroup> weGroups = this.baseMapper.selectWeGroupListByUserid(userId, corpId);
        //根据群聊id获取群员头像，拼接得到群头像
        for (WeGroup weGroup : weGroups) {
            List<WeGroupMemberDTO> weGroupMemberDTOS = weGroupMemberService.selectWeGroupMemberListByChatId(weGroup.getChatId());
            StringBuilder stringBuilder = new StringBuilder();
            //遍历9次拼接群头像
            int i = 0;
            for (WeGroupMemberDTO weGroupMemberDTO : weGroupMemberDTOS) {
                if (StringUtils.isEmpty(weGroupMemberDTO.getMemberAvatar())) {
                    continue;
                }
                if (i != 0) {
                    stringBuilder.append(WeConstans.COMMA);
                }
                stringBuilder.append(weGroupMemberDTO.getMemberAvatar());
                i++;
                if (i > 8) {
                    break;
                }
            }
            weGroup.setAvatar(stringBuilder.toString());
        }
        return weGroups;
    }

    @Override
    public List<WeGroup> listNoRelTag(String corpId, String tagIds, String ownerIds, String beginTime, String endTime) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        FindWeGroupDTO findWeGroupDTO = new FindWeGroupDTO();
        findWeGroupDTO.setCorpId(corpId);
        findWeGroupDTO.setTagIds(tagIds);
        findWeGroupDTO.setOwnerIds(ownerIds);
        findWeGroupDTO.setBeginTime(beginTime);
        findWeGroupDTO.setEndTime(endTime);
        return baseMapper.list(findWeGroupDTO);
    }
}
