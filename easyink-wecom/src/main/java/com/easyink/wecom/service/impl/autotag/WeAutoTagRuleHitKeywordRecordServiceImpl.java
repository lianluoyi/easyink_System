package com.easyink.wecom.service.impl.autotag;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.conversation.msgtype.TextVO;
import com.easyink.common.enums.autotag.AutoTagLabelTypeEnum;
import com.easyink.common.enums.autotag.AutoTagMatchTypeEnum;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagKeyword;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecord;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecordTagRel;
import com.easyink.wecom.domain.query.autotag.TagRuleRecordKeywordDetailQuery;
import com.easyink.wecom.domain.query.autotag.TagRuleRecordQuery;
import com.easyink.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easyink.wecom.domain.vo.autotag.record.CustomerCountVO;
import com.easyink.wecom.domain.vo.autotag.record.keyword.KeywordRecordDetailVO;
import com.easyink.wecom.domain.vo.autotag.record.keyword.KeywordTagRuleRecordVO;
import com.easyink.wecom.mapper.autotag.WeAutoTagRuleHitKeywordRecordMapper;
import com.easyink.wecom.mapper.autotag.WeAutoTagRuleHitKeywordRecordTagRelMapper;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.autotag.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户打标签记录表(WeAutoTagRuleHitKeywordRecord)表服务实现类
 *
 * @author tigger
 * @since 2022-03-02 14:51:27
 */
@Slf4j
@Service("weAutoTagRuleHitKeywordRecordService")
public class WeAutoTagRuleHitKeywordRecordServiceImpl extends ServiceImpl<WeAutoTagRuleHitKeywordRecordMapper, WeAutoTagRuleHitKeywordRecord> implements WeAutoTagRuleHitKeywordRecordService {
    @Autowired
    private WeCustomerService weCustomerService;
    @Autowired
    private WeAutoTagRuleService weAutoTagRuleService;
    @Autowired
    private WeAutoTagKeywordService weAutoTagKeywordService;
    @Autowired
    private WeAutoTagKeywordTagRelService weAutoTagKeywordTagRelService;
    @Autowired
    private WeAutoTagRuleHitKeywordRecordTagRelService weAutoTagRuleHitKeywordRecordTagRelService;
    @Autowired
    private WeAutoTagRuleHitKeywordRecordTagRelMapper weAutoTagRuleHitKeywordRecordTagRelMapper;

    /**
     * 关键词记录列表
     *
     * @param query
     * @return
     */
    @Override
    public List<KeywordTagRuleRecordVO> listKeywordRecord(TagRuleRecordQuery query) {
        return this.baseMapper.listKeywordRecord(query);
    }

    /**
     * 触发关键词详情列表
     *
     * @param query
     * @return
     */
    @Override
    public List<KeywordRecordDetailVO> listKeywordDetail(TagRuleRecordKeywordDetailQuery query) {
        return this.baseMapper.listKeywordDetail(query);
    }

    /**
     * 对客户进行关键词打标签
     *
     * @param dataList 消息列表
     * @param corpId   企业id
     */
    @Override
    public void makeTagToNewCustomer(List<ChatInfoVO> dataList, String corpId) {
        log.info(">>>>>>>>>>>>>>>准备进行关键词打标签");
        // 1.过滤可匹配的消息 1)发送消息的人是客户,且非群消息 2)是文本消息
        List<ChatInfoVO> textDataList = filterCanBeMatchMessage(dataList);
        if (CollectionUtils.isEmpty(textDataList)) {
            log.info("没有可匹配的消息类型,跳过关键词打标签");
            return;
        }

        // 查询所有启用得规则
        List<Long> allKeywordRuleIdList = weAutoTagRuleService.listRuleIdByLabelType(AutoTagLabelTypeEnum.KEYWORD.getType(), corpId);
        log.debug("关键词打标签可用规则列表: {}", allKeywordRuleIdList);

        if (CollectionUtils.isEmpty(allKeywordRuleIdList)) {
            log.info("没有对应的关键词标签设置,跳过关键词打标签");
            return;
        }
        // 查询存在员工范围的规则列表
        List<Long> hadUserScopeRuleIdList = weAutoTagRuleService.listContainUserScopeRuleIdList(corpId, AutoTagLabelTypeEnum.KEYWORD.getType());
        Map<Long, List<WeAutoTagKeyword>> notHadUserScopeRuleMap;
        List<Long> notHadUserScopeRuleIdList = new ArrayList<>(allKeywordRuleIdList);
        if (CollectionUtils.isNotEmpty(hadUserScopeRuleIdList)) {
            // 没有员工使用范围的规则列表
            notHadUserScopeRuleIdList.removeAll(hadUserScopeRuleIdList);
        }
        // 根据没有员工使用范围的规则查询对应的关键词规则并对规则id进行分组
        notHadUserScopeRuleMap = weAutoTagKeywordService.listKeywordGroupByRuleIdInRuleIdList(notHadUserScopeRuleIdList);
        // 准备的集合
        List<WeAutoTagRuleHitKeywordRecord> batchAddRecord = new ArrayList<>();
        List<WeAutoTagRuleHitKeywordRecordTagRel> batchAddTagRecord = new ArrayList<>();
        // k:员工和用户拼接key v:缓存的标签集合
        Map<String, Set<WeTag>> userCustomerTagMap = new HashMap<>();
        Map<String, Map<Long, List<WeAutoTagKeyword>>> userCacheMap = new HashMap<>();
        for (ChatInfoVO chatInfoVO : textDataList) {
            TextVO text = chatInfoVO.getText();
            String customerId = chatInfoVO.getFrom();
            // 单聊列表只有一个toList
            String userId = chatInfoVO.getTolist().get(0);
            final String userCustomerkey = userId + StrUtil.COLON + customerId;
            // k: 规则id, v: 触发的关键词列表
            Map<Long, List<String>> ruleKeywordMap = new HashMap<>();
            // 不存在员工范围的规则,直接进行匹配关键词判断
            matchText(notHadUserScopeRuleMap, text, userId, customerId, corpId, batchAddRecord, ruleKeywordMap);

            // 存在员工范围得规则列表
            if (CollectionUtils.isNotEmpty(hadUserScopeRuleIdList)) {
                // 从hadUserScopeRuleIdList中查询当前员工适用的规则,并按规则id分组
                Map<Long, List<WeAutoTagKeyword>> currentUserSuitableGroupByRuleIdMap;
                // 做一个用户的缓存,避免一直查询数据库
                if (ObjectUtils.isEmpty(userCacheMap.get(userId))) {
                    // 判断当前用户可用得规则id,并根据ruleId分组
                    currentUserSuitableGroupByRuleIdMap = weAutoTagKeywordService.listKeywordGroupByRuleIdByUserId(corpId, userId, hadUserScopeRuleIdList);
                    userCacheMap.put(userId, currentUserSuitableGroupByRuleIdMap);
                }
                currentUserSuitableGroupByRuleIdMap = userCacheMap.get(userId);
                matchText(currentUserSuitableGroupByRuleIdMap, text, userId, customerId, corpId, batchAddRecord, ruleKeywordMap);
            }
            // 组装记录
            for (Map.Entry<Long, List<String>> ruleKeywordEntry : ruleKeywordMap.entrySet()) {
                Long ruleId = ruleKeywordEntry.getKey();
                List<String> keywordList = ruleKeywordEntry.getValue();
                String keywordJoin = String.join(StrUtil.COMMA, keywordList);
                batchAddRecord.add(new WeAutoTagRuleHitKeywordRecord(corpId, ruleId, customerId, userId, keywordJoin,
                        text.getContent(), new Date()));
            }

            // 根据规则id列表组装标签记录
            if (CollectionUtils.isNotEmpty(ruleKeywordMap.keySet())) {

                List<WeAutoTagRuleHitKeywordRecordTagRel> tagRecordList = weAutoTagRuleHitKeywordRecordTagRelService.buildTagRecord(corpId, userId, customerId, ruleKeywordMap.keySet());
                batchAddTagRecord.addAll(tagRecordList);

                // 查询当前文本命中的规则列表设置的所有标签列表
                List<WeTag> currentUserCustomerMatchedtagList = weAutoTagKeywordTagRelService.getTagListByRuleIdList(ruleKeywordMap.keySet());
                Set<WeTag> cacheWeTagList = userCustomerTagMap.get(userCustomerkey);
                if (CollectionUtils.isEmpty(cacheWeTagList)) {
                    cacheWeTagList = new HashSet<>();
                    userCustomerTagMap.put(userCustomerkey, cacheWeTagList);
                }
                cacheWeTagList.addAll(currentUserCustomerMatchedtagList);
            }
        }

        // 添加对应的记录数据
        if (CollectionUtils.isNotEmpty(batchAddRecord)) {
            this.baseMapper.insertBatch(batchAddRecord);
        }
        if (CollectionUtils.isNotEmpty(batchAddTagRecord)) {
            weAutoTagRuleHitKeywordRecordTagRelMapper.insertBatch(batchAddTagRecord);
        }

        // 调用企业微信接口打标签
        if (!ObjectUtils.isEmpty(userCustomerTagMap)) {
            log.info("关键词调用接口打标签");
            for (Map.Entry<String, Set<WeTag>> userCustomerTagEntry : userCustomerTagMap.entrySet()) {
                String[] split = userCustomerTagEntry.getKey().split(StrUtil.COLON);
                String userId = split[0];
                String customerId = split[1];
                List<WeTag> weTagList = new ArrayList<>(userCustomerTagEntry.getValue());
                log.info("关键词打标签: 员工: {}, 客户: {}, 标签列表: {}", userId, customerId, weTagList.stream()
                        .map(WeTag::getName).collect(Collectors.toList()));
                weCustomerService.makeLabelbatch(Collections.singletonList(new WeMakeCustomerTagVO(customerId, userId, weTagList, corpId)), userId);
            }
        }
    }

    /**
     * 客户统计
     *
     * @param ruleId 规则id
     * @param corpId 企业id
     * @return
     */
    @Override
    public CustomerCountVO keywordCustomerCount(Long ruleId, String corpId) {
        StringUtils.checkCorpId(corpId);
        List<String> customerIdList = this.baseMapper.keywordCustomerCount(ruleId, corpId);
        return new CustomerCountVO(customerIdList.size(), (int) customerIdList.stream().distinct().count());
    }

    /**
     * 匹配文本数据
     *
     * @param groupByRuleIdMap 规则id和规则map
     * @param text             要匹配的文本数据
     * @param userId           员工id
     * @param customerId       客户id
     * @param corpId           企业id
     * @param batchAddRecord   添加记录的集合
     * @param ruleKeywordMap
     */
    private void matchText(Map<Long, List<WeAutoTagKeyword>> groupByRuleIdMap, TextVO text, String userId, String customerId,
                           String corpId, List<WeAutoTagRuleHitKeywordRecord> batchAddRecord, Map<Long, List<String>> ruleKeywordMap) {
        if (ObjectUtils.isEmpty(groupByRuleIdMap) || text == null || StringUtils.isBlank(userId)
                || StringUtils.isBlank(customerId) || StringUtils.isBlank(corpId) || batchAddRecord == null) {
            return;
        }
        for (Map.Entry<Long, List<WeAutoTagKeyword>> keywordEntry : groupByRuleIdMap.entrySet()) {
            Long ruleId = keywordEntry.getKey();
            List<WeAutoTagKeyword> keywordList = keywordEntry.getValue();
            for (WeAutoTagKeyword weAutoTagKeyword : keywordList) {
                AutoTagMatchTypeEnum matchTypeEnum = AutoTagMatchTypeEnum.getByType(weAutoTagKeyword.getMatchType());
                if (matchTypeEnum != null && matchTypeEnum.match(text.getContent(), weAutoTagKeyword.getKeyword())) {
                    // 添加匹配到的关键词
                    List<String> keywords = ruleKeywordMap.get(ruleId);
                    if (CollectionUtils.isEmpty(keywords)) {
                        keywords = new ArrayList<>();
                        ruleKeywordMap.put(ruleId, keywords);
                    }
                    keywords.add(weAutoTagKeyword.getKeyword());
                }
            }
        }
    }

    private List<ChatInfoVO> filterCanBeMatchMessage(List<ChatInfoVO> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }
        List<ChatInfoVO> textDataList = dataList.stream().filter(data -> {
            // 得到文本且非群消息
            boolean textSendDateFlag = StringUtils.isBlank(data.getRoomid()) && "send".equals(data.getAction()) && "text".equals(data.getMsgtype());
            // 过滤发送人是客户
            boolean customerSendFlag = WeConstans.ID_TYPE_EX.equals(StringUtils.weCustomTypeJudgment(data.getFrom()));
            boolean userSendFlag = WeConstans.ID_TYPE_USER.equals(StringUtils.weCustomTypeJudgment(data.getTolist().get(0)));
            return textSendDateFlag && customerSendFlag && userSendFlag;
        }).collect(Collectors.toList());
        return textDataList;
    }
}

