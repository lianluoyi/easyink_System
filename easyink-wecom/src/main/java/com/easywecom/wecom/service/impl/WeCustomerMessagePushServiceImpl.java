package com.easywecom.wecom.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.constant.GroupConstants;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.enums.GroupMessageType;
import com.easywecom.common.enums.MessageType;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.ExceptionUtil;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.common.utils.spring.SpringUtils;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.*;
import com.easywecom.wecom.domain.dto.WeCustomerMessageDTO;
import com.easywecom.wecom.domain.dto.WeCustomerMessageToUserDTO;
import com.easywecom.wecom.domain.dto.WeCustomerPushMessageDTO;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.message.*;
import com.easywecom.wecom.domain.vo.CustomerMessagePushVO;
import com.easywecom.wecom.mapper.WeCustomerMessageTimeTaskMapper;
import com.easywecom.wecom.service.*;
import com.easywecom.wecom.service.radar.WeRadarService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 类名： 群发消息服务类
 *
 * @author 佚名
 * @date 2021/10/13 16:35
 */
@Slf4j
@Service
public class WeCustomerMessagePushServiceImpl implements WeCustomerMessagePushService {
    private final WeCustomerMessageOriginalService weCustomerMessageOriginalService;
    private final WeCustomerMessageService weCustomerMessageService;
    private final WeCustomerSeedMessageService weCustomerSeedMessageService;
    private final WeCustomerService weCustomerService;
    private final WeGroupService weGroupService;
    private final WeCustomerMessgaeResultService weCustomerMessgaeResultService;
    private final WeCustomerMessageTimeTaskMapper customerMessageTimeTaskMapper;
    private final WeCorpAccountService corpAccountService;
    private final WeMessagePushClient messagePushClient;
    private final WeUserService weUserService;


    @Autowired
    public WeCustomerMessagePushServiceImpl(WeCustomerMessageOriginalService weCustomerMessageOriginalService, WeCustomerMessageService weCustomerMessageService, WeCustomerSeedMessageService weCustomerSeedMessageService, WeCustomerService weCustomerService, WeGroupService weGroupService, WeCustomerMessgaeResultService weCustomerMessgaeResultService, WeCustomerMessageTimeTaskMapper customerMessageTimeTaskMapper, WeCorpAccountService corpAccountService, WeMessagePushClient messagePushClient, WeUserService weUserService) {
        this.weCustomerMessageOriginalService = weCustomerMessageOriginalService;
        this.weCustomerMessageService = weCustomerMessageService;
        this.weCustomerSeedMessageService = weCustomerSeedMessageService;
        this.weCustomerService = weCustomerService;
        this.weGroupService = weGroupService;
        this.weCustomerMessgaeResultService = weCustomerMessgaeResultService;
        this.customerMessageTimeTaskMapper = customerMessageTimeTaskMapper;
        this.corpAccountService = corpAccountService;
        this.messagePushClient = messagePushClient;
        this.weUserService = weUserService;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWeCustomerMessagePush(CustomerMessagePushDTO customerMessagePushDTO, LoginUser loginUser) {
        final int maxMsgSize = 4000;
        String content = customerMessagePushDTO.getTextMessage().getContent();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(content)) {
            int length = content.getBytes().length;
            if (length > maxMsgSize) {
                throw new CustomException(ResultTip.TIP_MESSAGE_TO_LONG_ERROR);
            }
        }
        //如果获取不到会报错
        String corpId = loginUser.getCorpId();
        if (StrUtil.isNotBlank(customerMessagePushDTO.getSettingTime())) {
            if (DateUtils.diffTime(new Date(), DateUtil.parse(customerMessagePushDTO.getSettingTime(), DateUtils.YYYY_MM_DD_HH_MM)) > 0) {
                throw new CustomException(ResultTip.TIP_TIME_TASK_LESS_CURR);
            }
        }
        //校验CorpId
        StringUtils.checkCorpId(corpId);
        if (customerMessagePushDTO.getTextMessage() == null && CollectionUtils.isEmpty(customerMessagePushDTO.getAttachments())) {
            throw new CustomException(ResultTip.TIP_SEND_MESSAGE_ERROR);
        }
        List<WeCustomer> customers = Lists.newArrayList();
        List<WeGroup> groups = new ArrayList<>();
        //临时保存staffId
        final String tmpStaffId = customerMessagePushDTO.getStaffId();
        //构造客户/客户群
        buildCustomerGroups(customerMessagePushDTO, loginUser, customers, groups);
        //保存原始数据信息表
        long messageOriginalId = weCustomerMessageOriginalService.saveWeCustomerMessageOriginal(customerMessagePushDTO, tmpStaffId);

        long messageId = SnowFlakeUtil.nextId();
        customerMessagePushDTO.setMessageId(messageId);
        //保存映射信息
        int size = weCustomerMessgaeResultService.workerMappingCustomer(customerMessagePushDTO, messageId, customers, groups);
        //保存微信消息
        weCustomerMessageService.saveWeCustomerMessage(loginUser, messageId, messageOriginalId, customerMessagePushDTO, size);
        //保存分类消息信息
        weCustomerSeedMessageService.saveSeedMessage(customerMessagePushDTO, messageId);
        //异步发送
        CompletableFuture.runAsync(() -> {
            try {
                sendMessage(customerMessagePushDTO, messageId, customers);
            } catch (ParseException | JsonProcessingException e) {
                log.error("异步发送群发消息异常：ex:{}", ExceptionUtil.getExceptionMessage(e));
            }
        });

    }

    @Override
    public int getSendSize(CustomerMessagePushDTO customerMessagePushDTO, LoginUser loginUser) {
        StringUtils.checkCorpId(loginUser.getCorpId());
        List<WeCustomer> customers = Lists.newArrayList();
        List<WeGroup> groups = new ArrayList<>();
        //构造客户/客户群
        return buildCustomerGroups(customerMessagePushDTO, loginUser, customers, groups);
    }

    /**
     * 构造发送客户/群
     *
     * @param customerMessagePushDTO customerMessagePushDTO
     * @param loginUser              登录用户
     * @param customers              客户列表
     * @param groups                 群列表
     * @return 发送数量
     */
    private int buildCustomerGroups(CustomerMessagePushDTO customerMessagePushDTO, LoginUser loginUser, List<WeCustomer> customers, List<WeGroup> groups) {
        String corpId = loginUser.getCorpId();
        // 发给客户
        if (WeConstans.SEND_MESSAGE_CUSTOMER.equals(customerMessagePushDTO.getPushType())) {
            //查询客户信息列表
            customers.addAll(getExternalUserIds(corpId, customerMessagePushDTO.getPushRange(), customerMessagePushDTO.getStaffId(), customerMessagePushDTO.getDepartment(), customerMessagePushDTO.getTag(), customerMessagePushDTO.getFilterTags(), customerMessagePushDTO.getGender(), customerMessagePushDTO.getCustomerStartTime(), customerMessagePushDTO.getCustomerEndTime()));
            if (CollectionUtils.isEmpty(customers)) {
                throw new CustomException(ResultTip.TIP_NO_CUSTOMER);
            }
        } else {
            //发给客户群
            WeGroup weGroup = new WeGroup();
            weGroup.setCorpId(corpId);
            weGroup.setStatus(GroupConstants.OWNER_LEAVE_EXTEND_SUCCESS);
            if (WeConstans.SEND_MESSAGE_CUSTOMER_ALL.equals(customerMessagePushDTO.getPushRange())) {
                groups.addAll(weGroupService.selectWeGroupList(weGroup));
                List<String> ownerId = weGroupService.listOfOwnerId(corpId, loginUser.getDepartmentDataScope().split(WeConstans.COMMA));
                String staffIds = CollectionUtils.isNotEmpty(ownerId) ? String.join(WeConstans.COMMA, ownerId) : loginUser.getWeUser().getUserId();
                customerMessagePushDTO.setStaffId(staffIds);
            } else {
                Set<String> staffIds = new HashSet<>();
                if(StringUtils.isNotBlank(customerMessagePushDTO.getStaffId())){
                    staffIds.addAll(Arrays.asList(customerMessagePushDTO.getStaffId().split(StrUtil.COMMA)));
                }
                if(StringUtils.isNotBlank(customerMessagePushDTO.getDepartment())){
                    //查找部门下员工
                    List<String> userIdsByDepartment = weUserService.listOfUserId(loginUser.getCorpId(),customerMessagePushDTO.getDepartment().split(StrUtil.COMMA));
                    if(CollectionUtils.isNotEmpty(userIdsByDepartment)){
                        staffIds.addAll(userIdsByDepartment);
                    }
                }
                if (CollectionUtils.isEmpty(staffIds)) {
                    throw new CustomException(ResultTip.TIP_CHECK_STAFF);
                }
                customerMessagePushDTO.setStaffId(StringUtils.join(staffIds, WeConstans.COMMA));
                //通过员工id查询群列表
                weGroup.setUserIds(customerMessagePushDTO.getStaffId());
                //查出权限下的群
                groups.addAll(weGroupService.selectWeGroupList(weGroup));
            }
            if (CollectionUtils.isEmpty(groups)) {
                throw new CustomException(ResultTip.TIP_NO_GROUP);
            }
        }
        return CollectionUtils.isEmpty(customers) ? groups.size() : customers.size();
    }

    private void sendMessage(CustomerMessagePushDTO customerMessagePushDTO, Long messageId, List<WeCustomer> customers) throws ParseException, JsonProcessingException {
        //发送群发消息
        if (StringUtils.isEmpty(customerMessagePushDTO.getSettingTime())) {
            //立即发送
            weCustomerMessageService.sendMessage(customerMessagePushDTO, messageId, customers);
        } else {
            //存入定时任务
            WeCustomerMessageTimeTask timeTask = new WeCustomerMessageTimeTask(messageId, customerMessagePushDTO, customers
                    , DateUtils.getMillionSceondsBydate(customerMessagePushDTO.getSettingTime()));
            customerMessageTimeTaskMapper.saveWeCustomerMessageTimeTask(timeTask);
        }
    }

    @Override
    public List<CustomerMessagePushVO> customerMessagePushs(WeCustomerMessageDTO weCustomerMessageDTO) {
        if (weCustomerMessageDTO == null || StringUtils.isEmpty(weCustomerMessageDTO.getCorpId())) {
            return new ArrayList<>();
        }
        return weCustomerMessageOriginalService.customerMessagePushs(weCustomerMessageDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTimeTask(CustomerMessagePushDTO customerMessagePushDTO, LoginUser loginUser) throws ParseException {
        if (customerMessagePushDTO == null) {
            return;
        }
        StringUtils.checkCorpId(loginUser.getCorpId());
        customerMessagePushDTO.setCorpId(loginUser.getCorpId());
        //查询定时任务
        WeCustomerMessageTimeTask timeTask = getTimeTask(customerMessagePushDTO.getMessageId());
        //只能修改未发送的定时任务
        if (timeTask == null || Integer.valueOf(WeConstans.SEND).equals(timeTask.getSolved())) {
            throw new CustomException(ResultTip.TIP_MESSAGE_TASK_UPDATE_ERROR);
        }
        //设置修改后的值
        List<WeCustomer> customers = new ArrayList<>();
        List<WeGroup> groups = new ArrayList<>();
        //临时保存staffId 保存原始数据信息表时写入前端传进来的staffId
        final String tmpStaffId = customerMessagePushDTO.getStaffId();
        buildCustomerGroups(customerMessagePushDTO, loginUser, customers, groups);
        timeTask.setMessageInfo(customerMessagePushDTO);
        timeTask.setCustomersInfo(customers);
        if (StringUtils.isNotBlank(customerMessagePushDTO.getSettingTime())) {
            timeTask.setSettingTime(DateUtils.getMillionSceondsBydate(customerMessagePushDTO.getSettingTime()));
        }
        Long messageId = customerMessagePushDTO.getMessageId();
        //保存原始数据信息表
        WeCustomerMessage customerMessage = weCustomerMessageService.getById(messageId);
        WeCustomerMessageOriginal original = weCustomerMessageOriginalService.getById(customerMessage.getOriginalId());
        BeanUtils.copyProperties(customerMessagePushDTO, original);
        original.setStaffId(tmpStaffId);
        weCustomerMessageOriginalService.updateById(original);
        long messageOriginalId = original.getMessageOriginalId();

        //保存映射信息
        weCustomerMessgaeResultService.delete(messageId, loginUser.getCorpId());
        int size = weCustomerMessgaeResultService.workerMappingCustomer(customerMessagePushDTO, messageId, customers, groups);
        //保存微信消息
        weCustomerMessageService.deleteByMessageId(messageId, loginUser.getCorpId());
        weCustomerMessageService.saveWeCustomerMessage(loginUser, messageId, messageOriginalId, customerMessagePushDTO, size);
        //保存分类消息信息
        weCustomerSeedMessageService.deleteByMessageId(messageId, loginUser.getCorpId());
        weCustomerSeedMessageService.saveSeedMessage(customerMessagePushDTO, messageId);
        //判断是否定时
        if (StringUtils.isEmpty(customerMessagePushDTO.getSettingTime())) {
            customerMessageTimeTaskMapper.deleteById(timeTask.getTaskId());
            //异步发送
            CompletableFuture.runAsync(() -> {
                try {
                    weCustomerMessageService.sendMessage(customerMessagePushDTO, messageId, customers);
                } catch (Exception e) {
                    log.error("异步发送群发消息异常：messageId:{},ex:{}", messageId, ExceptionUtil.getExceptionMessage(e));
                }
            });
        } else {
            //修改
            customerMessageTimeTaskMapper.updateById(timeTask);
        }
    }

    @Override
    public CustomerMessagePushDTO getMessageInfo(Long messageId) {
        if (messageId == null) {
            return new CustomerMessagePushDTO();
        }
        WeCustomerMessageTimeTask timeTask = getTimeTask(messageId);
        return timeTask.getMessageInfo();
    }

    @Override
    public CustomerMessagePushDTO getCopyInfo(Long messageId) {
        if (messageId == null) {
            return new CustomerMessagePushDTO();
        }
        WeCustomerMessage customerMessage = weCustomerMessageService.getById(messageId);
        //定时任务调用获取定时任务信息接口
        if (Integer.valueOf(WeConstans.TIME_TASK).equals(customerMessage.getTimedTask())) {
            return getMessageInfo(messageId);
        }
        WeCustomerMessageOriginal original = weCustomerMessageOriginalService.getById(customerMessage.getOriginalId());
        List<WeCustomerSeedMessage> customerSeedMessages = weCustomerSeedMessageService.list(new LambdaQueryWrapper<WeCustomerSeedMessage>().eq(WeCustomerSeedMessage::getMessageId, messageId));
        CustomerMessagePushDTO customerMessagePushDTO = new CustomerMessagePushDTO();

        BeanUtils.copyProperties(original, customerMessagePushDTO);
        //构造附件
        customerMessagePushDTO.setAttachments(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(customerSeedMessages)) {
            buildAttachment(customerSeedMessages, customerMessagePushDTO);
        }
        return customerMessagePushDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long messageId, String corpId) {
        //删除原始数据信息表
        WeCustomerMessage customerMessage = weCustomerMessageService.getById(messageId);
        if (Integer.valueOf(WeConstans.TIME_TASK).equals(customerMessage.getTimedTask()) && WeConstans.NOT_SEND.equals(customerMessage.getCheckStatus())) {
            WeCustomerMessageOriginal original = weCustomerMessageOriginalService.getById(customerMessage.getOriginalId());
            weCustomerMessageOriginalService.removeById(original);
            //删除消息结果
            weCustomerMessgaeResultService.delete(messageId, corpId);
            //删除微信消息
            weCustomerMessageService.removeById(messageId);
            //删除分类消息信息
            weCustomerSeedMessageService.remove(new LambdaQueryWrapper<WeCustomerSeedMessage>().eq(WeCustomerSeedMessage::getMessageId, messageId));
            //删除定时任务
            customerMessageTimeTaskMapper.delete(new LambdaQueryWrapper<WeCustomerMessageTimeTask>().eq(WeCustomerMessageTimeTask::getMessageId, messageId));
        } else {
            throw new CustomException(ResultTip.TIP_MESSAGE_TASK_DELETE_ERROR);
        }
    }

    /**
     * 发提醒消息给员工
     *
     * @param weCustomerMessageToUserDTO 信息
     */
    @Override
    public void sendToUser(WeCustomerMessageToUserDTO weCustomerMessageToUserDTO) {
        String[] customers = weCustomerMessageToUserDTO.getCustomers().split("、");
        StringBuilder replaceMsg = new StringBuilder();
        if (ArrayUtils.isEmpty(customers)) {
            return;
        }
        if (WeConstans.SEND_MESSAGE_GROUP.equals(weCustomerMessageToUserDTO.getPushType())) {
            replaceMsg.append("「").append(customers[0]).append("」 等").append(customers.length).append("个客户群");
            sendAppMessage(weCustomerMessageToUserDTO.getUserId(), WeConstans.CUSTOMER_GROUP_MESSAGE_INFO, replaceMsg.toString(), weCustomerMessageToUserDTO.getCorpId());
        } else {
            replaceMsg.append(customers[0]).append(" 等").append(customers.length).append("个客户");
            sendAppMessage(weCustomerMessageToUserDTO.getUserId(), WeConstans.CUSTOMER_MESSAGE_INFO, replaceMsg.toString(), weCustomerMessageToUserDTO.getCorpId());
        }
    }

    /**
     * 发送应用消息（文本）
     *
     * @param userId     员工id
     * @param msg        消息
     * @param replaceMsg 替换消息
     * @param corpId     企业id
     */
    private void sendAppMessage(String userId, String msg, String replaceMsg, String corpId) {
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(corpId);
        String agentId = validWeCorpAccount.getAgentId();
        // 文本消息
        TextMessageDTO text = new TextMessageDTO();
        StringBuilder content = new StringBuilder();
        //设置发送者 发送给企业员工
        pushDto.setTouser(userId);
        msg = msg.replace(WeConstans.REPLACE_MSG, replaceMsg);
        content.append(msg);
        text.setContent(content.toString());
        pushDto.setAgentid(Integer.valueOf(agentId));
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        log.debug("发送员工提醒信息：toUser:{}", userId);
        messagePushClient.sendMessageToUser(pushDto, agentId, corpId);
    }

    private WeCustomerMessageTimeTask getTimeTask(Long messageId) {
        final WeCustomerMessageTimeTask timeTask = customerMessageTimeTaskMapper.getTimeTask(messageId);
        if (CollectionUtils.isNotEmpty(timeTask.getMessageInfo().getAttachments())) {
            timeTask.getMessageInfo().getAttachments().forEach(item -> {
                if (GroupMessageType.RADAR.getType().equals(item.getMsgtype())) {
                    item.getRadarMessage().setRadar(SpringUtils.getBean(WeRadarService.class).getRadar(timeTask.getMessageInfo().getCorpId(), item.getRadarMessage().getRadarId()));
                }
            });
        }
        return timeTask;
    }

    /**
     * 客户的外部联系人id列表，仅在chat_type为single时有效，不可与sender同时为空，最多可传入1万个客户
     *
     * @param corpId     企业id
     * @param pushRange  消息范围 0 全部客户  1 指定客户
     * @param staffId    员工id
     * @param tag        客户标签id列表
     * @param filterTags 过滤用的标签
     * @param gender     性别
     * @return {@link List<WeCustomer>} 客户的外部联系人id列表
     */
    @Override
    public List<WeCustomer> getExternalUserIds(String corpId, String pushRange, String staffId, String departmentIds, String tag, String filterTags, Integer gender, Date startTime, Date endTime) {
        //校验CorpId
        StringUtils.checkCorpId(corpId);
        if (pushRange.equals(WeConstans.SEND_MESSAGE_CUSTOMER_ALL)) {
            //从redis中读取数据
            WeCustomerPushMessageDTO weCustomer = new WeCustomerPushMessageDTO();
            weCustomer.setCorpId(corpId);
            return weCustomerService.selectWeCustomerListNoRel(weCustomer);
        } else {
            //按条件查询客户
            WeCustomerPushMessageDTO weCustomer = new WeCustomerPushMessageDTO();
            weCustomer.setUserIds(staffId);
            weCustomer.setDepartmentIds(departmentIds);
            weCustomer.setCorpId(corpId);
            weCustomer.setTagIds(tag);
            weCustomer.setFilterTags(filterTags);
            weCustomer.setGender(gender);
            weCustomer.setCustomerStartTime(startTime);
            weCustomer.setCustomerEndTime(endTime);
            return weCustomerService.selectWeCustomerListNoRel(weCustomer);
        }
    }

    /**
     * 构造附件
     *
     * @param customerSeedMessages 子消息表
     */
    private void buildAttachment(List<WeCustomerSeedMessage> customerSeedMessages, CustomerMessagePushDTO customerMessagePushDTO) {
        List<Attachment> attachments = customerMessagePushDTO.getAttachments();
        for (WeCustomerSeedMessage customerSeedMessage : customerSeedMessages) {
            Attachment attachment = new Attachment();
            String msgtype = customerSeedMessage.getMessageType();
            //文字
            if (GroupMessageType.TEXT.getType().equals(msgtype)) {
                TextMessageDTO textMessageDTO = new TextMessageDTO();
                textMessageDTO.setContent(customerSeedMessage.getContent());
                customerMessagePushDTO.setTextMessage(textMessageDTO);
                continue;
            }
            attachment.setMsgtype(msgtype);
            //图片
            if (GroupMessageType.IMAGE.getType().equals(msgtype)) {
                ImageMessageDTO imageMessage = new ImageMessageDTO();
                imageMessage.setPic_url(customerSeedMessage.getPicUrl());
                imageMessage.setTitle(customerSeedMessage.getPicName());
                attachment.setImageMessage(imageMessage);
            }
            //小程序
            else if (GroupMessageType.MINIPROGRAM.getType().equals(msgtype)) {
                MiniprogramMessageDTO miniprogramMessage = new MiniprogramMessageDTO();
                BeanUtils.copyProperties(customerSeedMessage, miniprogramMessage);
                miniprogramMessage.setTitle(customerSeedMessage.getMiniprogramTitle());
                attachment.setMiniprogramMessage(miniprogramMessage);
            }
            //链接
            else if (GroupMessageType.LINK.getType().equals(msgtype)) {
                LinkMessageDTO linkMessage = new LinkMessageDTO();
                linkMessage.setUrl(customerSeedMessage.getLinkUrl());
                linkMessage.setTitle(customerSeedMessage.getLinkTitle());
                linkMessage.setDesc(customerSeedMessage.getLinDesc());
                linkMessage.setPicurl(customerSeedMessage.getPicUrl());
                attachment.setLinkMessage(linkMessage);
            } else if (GroupMessageType.RADAR.getType().equals(msgtype)) {
                RadarMessageDTO radarMessage = new RadarMessageDTO();
                radarMessage.setRadarId(customerSeedMessage.getRadarId());
                radarMessage.setRadar(SpringUtils.getBean(WeRadarService.class).getRadar(customerMessagePushDTO.getCorpId(), radarMessage.getRadarId()));
                attachment.setRadarMessage(radarMessage);
            }
            //视频
            else if (GroupMessageType.VIDEO.getType().equals(msgtype)) {
                VideoDTO videoDTO = new VideoDTO();
                videoDTO.setVideoUrl(customerSeedMessage.getVideoUrl());
                videoDTO.setTitle(customerSeedMessage.getVideoName());
                videoDTO.setCoverUrl(customerSeedMessage.getPicUrl());
                videoDTO.setSize(customerSeedMessage.getSize());
                attachment.setVideoDTO(videoDTO);
            }
            //文件
            else if (GroupMessageType.FILE.getType().equals(msgtype)) {
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFileUrl(customerSeedMessage.getFileUrl());
                fileDTO.setTitle(customerSeedMessage.getFileName());
                attachment.setFileDTO(fileDTO);
            }
            attachments.add(attachment);
        }
    }
}
