package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitGroupRecord;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitGroupRecordTagRel;
import com.easyink.wecom.domain.query.autotag.TagRuleRecordQuery;
import com.easyink.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easyink.wecom.domain.vo.autotag.record.CustomerCountVO;
import com.easyink.wecom.domain.vo.autotag.record.group.GroupTagRuleRecordVO;
import com.easyink.wecom.mapper.autotag.WeAutoTagRuleHitGroupRecordMapper;
import com.easyink.wecom.mapper.autotag.WeAutoTagRuleHitGroupRecordTagRelMapper;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.WeGroupService;
import com.easyink.wecom.service.autotag.WeAutoTagGroupSceneGroupRelService;
import com.easyink.wecom.service.autotag.WeAutoTagGroupSceneTagRelService;
import com.easyink.wecom.service.autotag.WeAutoTagRuleHitGroupRecordService;
import com.easyink.wecom.service.autotag.WeAutoTagRuleHitGroupRecordTagRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户打标签记录表(WeAutoTagRuleHitGroupRecord)表服务实现类
 *
 * @author tigger
 * @since 2022-03-02 15:42:27
 */
@Slf4j
@Service("weAutoTagRuleHitGroupRecordService")
public class WeAutoTagRuleHitGroupRecordServiceImpl extends ServiceImpl<WeAutoTagRuleHitGroupRecordMapper, WeAutoTagRuleHitGroupRecord> implements WeAutoTagRuleHitGroupRecordService {

    @Autowired
    private WeCustomerService weCustomerService;
    @Autowired
    private WeFlowerCustomerRelService weFlowerCustomerRelService;
    @Autowired
    private WeGroupService WeGroupService;
    @Autowired
    private WeAutoTagGroupSceneGroupRelService weAutoTagGroupSceneGroupRelService;
    @Autowired
    private WeAutoTagGroupSceneTagRelService weAutoTagGroupSceneTagRelService;
    @Autowired
    private WeAutoTagRuleHitGroupRecordTagRelService weAutoTagRuleHitGroupRecordTagRelService;

    @Autowired
    private WeAutoTagRuleHitGroupRecordTagRelMapper weAutoTagRuleHitGroupRecordTagRelMapper;

    /**
     * 群记录列表
     *
     * @param query
     * @return
     */
    @Override
    public List<GroupTagRuleRecordVO> listGroupRecord(TagRuleRecordQuery query) {
        return this.baseMapper.listGroupRecord(query);
    }

    /**
     * 新客进群打标签
     *
     * @param chatId                群id
     * @param newJoinCustomerIdList 客户id列表
     * @param corpId                企业id
     */
    @Override
    public void makeTagToNewCustomer(String chatId, List<String> newJoinCustomerIdList, String corpId) {
        log.info(">>>>>>>>>>执行客户打标签");
        if (StringUtils.isBlank(chatId) || CollectionUtils.isEmpty(newJoinCustomerIdList) || StringUtils.isBlank(corpId)) {
            log.error("参数异常,跳过打标签,chatId: {}, newJoinCustomerIdList: {}, corpId: {}", chatId, newJoinCustomerIdList, corpId);
            return;
        }
        // 从关系表里查询判断客户是否是企业下的客户
        Iterator<String> iterator = newJoinCustomerIdList.iterator();
        if (iterator.hasNext()) {
            String customerId = iterator.next();
            int count = weFlowerCustomerRelService.count(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                    .eq(WeFlowerCustomerRel::getExternalUserid, customerId)
                    .eq(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.NORMAL.getCode())
                    .eq(WeFlowerCustomerRel::getCorpId, corpId));
            if (count == 0) {
                iterator.remove();
            }
        }
        // 查询群名称信息
        String groupName = WeGroupService.getOne(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getChatId, chatId)
                .select(WeGroup::getGroupName)).getGroupName();
        // 1.查询包含该群id的场景id列表(SceneIdList),并按规则分组
        List<WeAutoTagRuleHitGroupRecord> batchAddRecordList = new ArrayList<>();
        List<WeAutoTagRuleHitGroupRecordTagRel> batchAddTagRelList = new ArrayList<>();
        List<WeTag> allTagList = new ArrayList<>();
        // 获取命中的规则对应的标签列表
        Map<Long, List<WeTag>> tagListGroupByRuleIdMap = weAutoTagGroupSceneGroupRelService.getTagListGroupByRuleIdByChatId(chatId);
        if (ObjectUtils.isEmpty(tagListGroupByRuleIdMap)) {
            log.info("当前群没有设置标签规则,跳过打标签, chatId: {}", chatId);
            return;
        }
        tagListGroupByRuleIdMap.forEach((ruleId, tagList) -> {
            List<WeAutoTagRuleHitGroupRecord> recordList = this.buildRecord(ruleId, newJoinCustomerIdList, chatId, groupName, corpId);
            List<WeAutoTagRuleHitGroupRecordTagRel> tagRelList = weAutoTagRuleHitGroupRecordTagRelService.buildTagRel(ruleId, newJoinCustomerIdList, chatId, tagList);
            allTagList.addAll(tagList);
            batchAddRecordList.addAll(recordList);
            batchAddTagRelList.addAll(tagRelList);
        });
        // 2.添加记录
        if (CollectionUtils.isNotEmpty(batchAddRecordList)) {
            this.baseMapper.insertOrUpdateBatch(batchAddRecordList);
        }
        if (CollectionUtils.isNotEmpty(batchAddTagRelList)) {
            weAutoTagRuleHitGroupRecordTagRelMapper.insertBatch(batchAddTagRelList);
        }
        // 3.调用企业微信接口打标签
        if (CollectionUtils.isNotEmpty(allTagList)) {
            log.info("群打标签标签列表: {}", allTagList.stream().map(WeTag::getTagId).collect(Collectors.toList()));
            for (String customerId : newJoinCustomerIdList) {
                // 查询客户所属的员工列表,loop:打标签
                List<String> userIdList = weFlowerCustomerRelService.listUpUserIdListByCustomerId(customerId, corpId);
                for (String userId : userIdList) {
                    log.info("入群打标签: 员工: {}, 客户: {}", userId, customerId);
                    weCustomerService.batchMakeLabel(Collections.singletonList(new WeMakeCustomerTagVO(customerId, userId, allTagList, corpId)), userId);
                }
            }
        }

    }

    /**
     * 构建群记录实体列表
     *
     * @param ruleId                规则id
     * @param newJoinCustomerIdList 涉及的客户id列表
     * @param chatId                群id
     * @param groupName             群名称
     * @param corpId                企业id
     * @return
     */
    @Override
    public List<WeAutoTagRuleHitGroupRecord> buildRecord(Long ruleId, List<String> newJoinCustomerIdList, String chatId, String groupName, String corpId) {
        List<WeAutoTagRuleHitGroupRecord> recordList = new ArrayList<>(newJoinCustomerIdList.size());
        if (ruleId == null || CollectionUtils.isEmpty(newJoinCustomerIdList) || StringUtils.isBlank(chatId)
                || StringUtils.isBlank(groupName) || StringUtils.isBlank(corpId)) {
            log.error("参数异常,取消构建群记录数据");
            return recordList;
        }
        // 查询群
        Date date = new Date();
        for (String customerId : newJoinCustomerIdList) {
            recordList.add(new WeAutoTagRuleHitGroupRecord(ruleId, corpId, customerId, chatId, groupName, date));
        }
        return recordList;
    }

    /**
     * 群客户统计
     *
     * @param ruleId 规则id
     * @param corpId 企业id
     * @return
     */
    @Override
    public CustomerCountVO groupCustomerCount(Long ruleId, String corpId) {
        List<String> customerIdList = this.baseMapper.groupCustomerCount(ruleId, corpId);
        int size = customerIdList.size();
        return new CustomerCountVO(size, size);
    }


}

