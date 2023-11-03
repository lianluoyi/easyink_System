package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.MethodParamType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.BaseException;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.wecom.annotation.Convert2Cipher;
import com.easyink.wecom.client.WeCustomerGroupClient;
import com.easyink.wecom.domain.WeGroupMember;
import com.easyink.wecom.domain.dto.FindWeGroupMemberDTO;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.wecom.domain.dto.customer.CustomerGroupDetail;
import com.easyink.wecom.domain.dto.customer.CustomerGroupMember;
import com.easyink.wecom.domain.vo.FindWeGroupMemberCountVO;
import com.easyink.wecom.mapper.WeGroupMemberMapper;
import com.easyink.wecom.service.WeGroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名： WeGroupMemberServiceImpl
 *
 * @author 佚名
 * @date 2021/8/26 20:56
 */
@Slf4j
@Service
public class WeGroupMemberServiceImpl extends ServiceImpl<WeGroupMemberMapper, WeGroupMember> implements WeGroupMemberService {

    @Autowired
    private WeCustomerGroupClient weCustomerGroupClient;

    @Autowired
    private WeGroupMemberService weGroupMemberService;

    @Autowired
    private WeGroupMemberMapper weGroupMemberMapper;

    @Override
    public List<WeGroupMember> selectWeGroupMemberList(FindWeGroupMemberDTO weGroupMember) {
        if (weGroupMember == null || StringUtils.isBlank(weGroupMember.getChatId()) || StringUtils.isBlank(weGroupMember.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //开始时间
        if (StringUtils.isNotBlank(weGroupMember.getStartTime())) {
            if (!DateUtils.isMatchFormat(weGroupMember.getStartTime(), DateUtils.YYYY_MM_DD)) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
            String beginDay = DateUtils.parseBeginDay(weGroupMember.getStartTime());
            weGroupMember.setStartTime(beginDay);
        }
        //结束时间
        if (StringUtils.isNotBlank(weGroupMember.getEndTime())) {
            if (!DateUtils.isMatchFormat(weGroupMember.getEndTime(), DateUtils.YYYY_MM_DD)) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
            String endDay = DateUtils.parseEndDay(weGroupMember.getEndTime());
            weGroupMember.setEndTime(endDay);
        }
        return baseMapper.selectWeGroupMember(weGroupMember);
    }

    @Override
    @Convert2Cipher(paramType = MethodParamType.STRUCT)
    public FindWeGroupMemberCountVO selectWeGroupMemberCount(FindWeGroupMemberDTO findWeGroupMemberDTO) {
        if (findWeGroupMemberDTO == null || StringUtils.isBlank(findWeGroupMemberDTO.getCorpId()) || StringUtils.isBlank(findWeGroupMemberDTO.getChatId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        FindWeGroupMemberCountVO countVO = baseMapper.selectWeGroupMemberCount(findWeGroupMemberDTO);
        if (countVO == null) {
            countVO = new FindWeGroupMemberCountVO();
        }
        return countVO;
    }

    @Override
    public List<WeGroupMemberDTO> selectWeGroupMemberListByChatIdList(List<String> chatIdList) {
        if (CollectionUtils.isEmpty(chatIdList)){
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        return this.baseMapper.selectWeGroupMemberListByChatIdList(chatIdList);
    }

    @Override
    public List<WeGroupMemberDTO> selectWeGroupMemberListByChatId(String chatId) {
        if (StringUtils.isBlank(chatId)){
            log.error("群id不能为空");
            throw new BaseException("根据群id获取群聊列表失败");
        }
        return this.baseMapper.selectWeGroupMemberListByChatId(chatId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchWeGroupMember(String chatId, String corpId) {
        if (StringUtils.isAnyBlank(chatId,corpId)){
            log.error("群聊id和公司id不能为空，corpId：{}，chatId：{}", corpId , chatId);
            throw new BaseException("更新群聊列表失败");
        }
        Long timeTerm = 1000L;
        //获取客户详情数据
        CustomerGroupDetail customerGroupDetail = weCustomerGroupClient.groupChatDetail(
                new CustomerGroupDetail().new Params(chatId, WeConstans.NEED_NAME), corpId
        );
        //删除客户数据
        weGroupMemberService.remove(new LambdaQueryWrapper<WeGroupMember>()
                .eq(WeGroupMember::getCorpId, corpId)
                .in(WeGroupMember::getChatId, chatId));

        //添加客户数据
        List<WeGroupMember> weGroupMembers = new ArrayList<>();
        if (customerGroupDetail.getErrcode().equals(WeConstans.WE_SUCCESS_CODE) && CollUtil.isNotEmpty(customerGroupDetail.getGroup_chat())) {
            for (CustomerGroupDetail.GroupChat groupChat : customerGroupDetail.getGroup_chat()) {
                //获取群聊列表，将数据补充加入到list集合
                List<CustomerGroupMember> memberLists = groupChat.getMember_list();
                if (CollUtil.isNotEmpty(memberLists)) {
                    for (CustomerGroupMember member : memberLists) {
                        String invitorUserId = member.getInvitor() != null && member.getInvitor().getUserid() != null ? member.getInvitor().getUserid() : "";
                        //由于群聊列表已删除，则id由雪花算法生成一个
                        weGroupMembers.add(
                                WeGroupMember.builder()
                                        .id(SnowFlakeUtil.nextId())
                                        .chatId(groupChat.getChat_id())
                                        .corpId(corpId)
                                        .userId(member.getUserid())
                                        .joinTime(DateUtil.date(member.getJoin_time() * timeTerm))
                                        .joinScene(member.getJoin_scene())
                                        .joinType(member.getType())
                                        .unionId(member.getUnionid())
                                        .memberName(member.getName())
                                        .invitor(invitorUserId)
                                        .build()
                        );
                    }
                }
            }
        }
        //保存到数据库
        if (CollUtil.isNotEmpty(weGroupMembers)) {
            weGroupMemberService.saveBatch(weGroupMembers);
        }
    }

    @Override
    public Integer batchInsert(List<WeGroupMember> list) {
        return weGroupMemberMapper.batchInsert(list);
    }
}
