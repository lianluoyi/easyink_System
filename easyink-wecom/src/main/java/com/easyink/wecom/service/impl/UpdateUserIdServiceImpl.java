package com.easyink.wecom.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomerMessage;
import com.easyink.wecom.domain.WeCustomerMessageOriginal;
import com.easyink.wecom.domain.WeCustomerMessgaeResult;
import com.easyink.wecom.domain.WeOperationsCenterCustomerSopFilterEntity;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskEntity;
import com.easyink.wecom.mapper.WeCustomerMessageMapper;
import com.easyink.wecom.mapper.WeCustomerMessageOriginalMapper;
import com.easyink.wecom.mapper.WeCustomerMessgaeResultMapper;
import com.easyink.wecom.mapper.WeOperationsCenterCustomerSopFilterMapper;
import com.easyink.wecom.mapper.moment.WeMomentTaskMapper;
import com.easyink.wecom.service.UpdateUserIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName： UpdateUserIdServiceImpl
 * 更新userI用“，”分隔的表
 *
 * @author wx
 * @date 2022/8/23 19:23
 */
@Service
public class UpdateUserIdServiceImpl implements UpdateUserIdService {


    final private WeMomentTaskMapper weMomentTaskMapper;

    final private WeOperationsCenterCustomerSopFilterMapper weOperationsCenterCustomerSopFilterMapper;

    final private WeCustomerMessageOriginalMapper customerMessageOriginalMapper;

    final private WeCustomerMessageMapper weCustomerMessageMapper;

    final private WeCustomerMessgaeResultMapper weCustomerMessgaeResultMapper;


    @Autowired
    public UpdateUserIdServiceImpl(WeMomentTaskMapper weMomentTaskMapper, WeOperationsCenterCustomerSopFilterMapper weOperationsCenterCustomerSopFilterMapper, WeCustomerMessageOriginalMapper customerMessageOriginalMapper, WeCustomerMessageMapper weCustomerMessageMapper, WeCustomerMessgaeResultMapper weCustomerMessgaeResultMapper) {
        this.weMomentTaskMapper = weMomentTaskMapper;
        this.weOperationsCenterCustomerSopFilterMapper = weOperationsCenterCustomerSopFilterMapper;
        this.customerMessageOriginalMapper = customerMessageOriginalMapper;
        this.weCustomerMessageMapper = weCustomerMessageMapper;
        this.weCustomerMessgaeResultMapper = weCustomerMessgaeResultMapper;
    }

    /**
     * 更新weMomentTask users
     * 该表存储userId是用","分隔的所以要先提取userId再替换openUserId再拼接起来存入
     *
     * @param openUserIdMap
     * @param corpId
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public void updateMomentTaskUserIds(Map<String, String> openUserIdMap, String corpId) {
        //we_moment_task(users
        final Map<WeMomentTaskEntity, String> weMomentTaskEntityMap = weMomentTaskMapper.selectList(new LambdaQueryWrapper<WeMomentTaskEntity>()
                        .eq(WeMomentTaskEntity::getCorpId, corpId))
                .stream().collect(Collectors.toMap(rel -> rel, WeMomentTaskEntity::getUsers));
        for (Map.Entry<WeMomentTaskEntity, String> entry : weMomentTaskEntityMap.entrySet()) {
            final StringBuilder openUserIds = new StringBuilder();
            for(String item: entry.getValue().split(StrUtil.COMMA)){
                if(StringUtils.isBlank(openUserIdMap.get(item))){
                    continue;
                }
                openUserIds.append(openUserIdMap.get(item)).append(StrUtil.COMMA);
            }
            if(StringUtils.isBlank(openUserIds)){
                continue;
            }
            //删除多余的逗号
            openUserIds.deleteCharAt(openUserIds.lastIndexOf(WeConstans.COMMA));
            entry.getKey().setUsers(String.valueOf(openUserIds));
            weMomentTaskMapper.updateById(entry.getKey());
        }
    }

    /**
     * 更新we_operations_center_customer_sop_filter(users
     * 该表存储userId是用","分隔的所以要先提取userId再替换openUserId再拼接起来存入
     *
     * @param openUserIdMap
     * @param corpId
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public void updateSOPFilterUserIds(Map<String, String> openUserIdMap, String corpId) {
        final Map<WeOperationsCenterCustomerSopFilterEntity, String> weOperationsCenterCustomerSopFilterEntityMap = weOperationsCenterCustomerSopFilterMapper.selectList(new LambdaQueryWrapper<WeOperationsCenterCustomerSopFilterEntity>()
                        .eq(WeOperationsCenterCustomerSopFilterEntity::getCorpId, corpId))
                .stream().collect(Collectors.toMap(rel -> rel, WeOperationsCenterCustomerSopFilterEntity::getUsers));
        for (Map.Entry<WeOperationsCenterCustomerSopFilterEntity, String> entry : weOperationsCenterCustomerSopFilterEntityMap.entrySet()) {
            List <String> openUserIds = new ArrayList<>();
            for(String item: entry.getValue().split(StrUtil.COMMA)){
                if(StringUtils.isBlank(openUserIdMap.get(item))){
                    continue;
                }
                openUserIds.add(openUserIdMap.get(item));
            }
            if(CollectionUtils.isEmpty(openUserIds)){
                continue;
            }
            entry.getKey().setUsers(StringUtils.join(openUserIds, StrUtil.COMMA));
            weOperationsCenterCustomerSopFilterMapper.updateById(entry.getKey());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public void updateCustomerOriginal(Map<String, String> openUserIdMap, Map<String, String> openExternalUserIdMap, String corpId) {
        final Map<WeCustomerMessageOriginal, String> customerMessageOriginalMap = customerMessageOriginalMapper.selectList(new LambdaQueryWrapper<WeCustomerMessageOriginal>().eq(WeCustomerMessageOriginal::getCorpId, corpId))
                .stream().collect(Collectors.toMap(rel -> rel, WeCustomerMessageOriginal::getStaffId));
        for (Map.Entry<WeCustomerMessageOriginal, String> entry : customerMessageOriginalMap.entrySet()) {
            List <String> openUserIds = new ArrayList<>();
            for(String item: entry.getValue().split(StrUtil.COMMA)){
                if(StringUtils.isBlank(openUserIdMap.get(item))){
                    continue;
                }
                openUserIds.add(openUserIdMap.get(item));
            }
            if(CollectionUtils.isEmpty(openUserIds)){
                continue;
            }
            entry.getKey().setStaffId(StringUtils.join(openUserIds, StrUtil.COMMA));
            customerMessageOriginalMapper.updateById(entry.getKey());
        }
        //更新we_customer_message<sender
        List<Long> messageIdList = new ArrayList<>();
        for (Map.Entry<WeCustomerMessageOriginal, String> entry : customerMessageOriginalMap.entrySet()) {
            final Map<WeCustomerMessage, String> weCustomerMessageMap = weCustomerMessageMapper.selectList(new LambdaQueryWrapper<WeCustomerMessage>()
                            .in(WeCustomerMessage::getOriginalId, entry.getKey().getMessageOriginalId()))
                    .stream().collect(Collectors.toMap(rel -> rel, WeCustomerMessage::getSender));
            for (Map.Entry<WeCustomerMessage, String> messageEntry : weCustomerMessageMap.entrySet()) {
                List <String> openUserIds = new ArrayList<>();
                for(String item: entry.getValue().split(StrUtil.COMMA)){
                    if(StringUtils.isBlank(openUserIdMap.get(item))){
                        continue;
                    }
                    openUserIds.add(openUserIdMap.get(item));
                }
                if(CollectionUtils.isEmpty(openUserIds)){
                    continue;
                }
                //删除多余的逗号
                messageEntry.getKey().setSender(StringUtils.join(openUserIds, StrUtil.COMMA));
                weCustomerMessageMapper.updateById(messageEntry.getKey());
                messageIdList.add(messageEntry.getKey().getMessageId());
            }
        }
        //更新we_customer_messgaeresult
        for(Long messageId : messageIdList){
            final Map<WeCustomerMessgaeResult, String> weCustomerMessageResultMap = weCustomerMessgaeResultMapper.selectList(new LambdaQueryWrapper<WeCustomerMessgaeResult>().eq(WeCustomerMessgaeResult::getMessageId, messageId))
                    .stream().collect(Collectors.toMap(rel -> rel, WeCustomerMessgaeResult::getUserid));
            for(Map.Entry<WeCustomerMessgaeResult, String> messageResultEntry : weCustomerMessageResultMap.entrySet()){
                if(openUserIdMap.get(messageResultEntry.getKey().getUserid()) == null){
                    continue;
                }
                messageResultEntry.getKey().setUserid(openUserIdMap.get(messageResultEntry.getKey().getUserid()));
                //更新external_userid
                messageResultEntry.getKey().setExternalUserid(openExternalUserIdMap.get(messageResultEntry.getKey().getExternalUserid()));
                weCustomerMessgaeResultMapper.updateById(messageResultEntry.getKey());
            }
        }

    }



}
