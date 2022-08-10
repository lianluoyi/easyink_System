package com.easyink.quartz.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.conversation.msgtype.MsgTypeEnum;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.elasticsearch.ElasticSearch;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.wecom.domain.WeCustomerMessageTimeTask;
import com.easyink.wecom.mapper.WeCustomerMessageTimeTaskMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.autotag.WeAutoTagRuleHitKeywordRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tencent.wework.FinanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 定时任务调度测试
 *
 * @author admin
 */
@Slf4j
@Component("ryTask")
public class RyTask {
    @Autowired
    private ElasticSearch elasticSearch;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private WeChatContactMappingService weChatContactMappingService;
    @Autowired
    private WeSensitiveService weSensitiveService;
    @Autowired
    private WeSensitiveActHitService weSensitiveActHitService;
    @Autowired
    private WeCustomerMessageTimeTaskMapper customerMessageTimeTaskMapper;

    @Autowired
    private WeCustomerMessageService weCustomerMessageService;
    @Autowired
    private WeCorpAccountService iWxCorpAccountService;
    @Autowired
    private WeAutoTagRuleHitKeywordRecordService weAutoTagRuleHitKeywordRecordService;


    public void FinanceTask() throws IOException {
        log.info(">>>>>>执行会话存档定时任务");
        List<WeCorpAccount> weCorpAccountList = iWxCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        if (CollectionUtils.isEmpty(weCorpAccountList)) {
            log.error("没有可用的企业配置");
            return;
        }
        weCorpAccountList.parallelStream().forEach(wxCorpAccount -> {
            String corpId = null;
            try {
                corpId = wxCorpAccount.getCorpId();
                if (StringUtils.isAnyBlank(corpId, wxCorpAccount.getChatSecret())) {
                    log.error("公司ID:{},会话存档Secret:{}", corpId, wxCorpAccount.getChatSecret());
                    return;
                }
                String chatDataIndex = WeConstans.getChatDataIndex(corpId);
                // 创建索引
                elasticSearch.createIndex2(chatDataIndex, elasticSearch.getFinanceMapping());
                //从缓存中获取消息标识
                Object seqObject = Optional.ofNullable(redisCache.getCacheObject(WeConstans.getContactSeqKey(corpId))).orElse(0L);
                Long seqLong = Long.valueOf(String.valueOf(seqObject));
                AtomicLong index = new AtomicLong(seqLong);
                if (index.get() == 0) {
                    setRedisCacheSeqValue(index, corpId);
                }
                log.info(">>>>>>>corpId:{}, seq:{}", corpId, index.get());
                FinanceUtils.initSDK(corpId, wxCorpAccount.getChatSecret());
                List<ChatInfoVO> chatDataList = FinanceUtils.getChatData(index.get(), "", "", redisCache, corpId);

                // 单独处理测回消息，因为可能会批量
                setRevokeInfo1(chatDataList, corpId);

                if (CollUtil.isNotEmpty(chatDataList)) {
                    // 保存消息映射关系
                    List<ChatInfoVO> elasticSearchEntities = weChatContactMappingService.saveWeChatContactMapping1(corpId, chatDataList);
                    //获取敏感行为命中信息
                    weSensitiveActHitService.hitWeSensitiveAct1(corpId, chatDataList);
                    elasticSearch.insertBatch1(WeConstans.getChatDataIndex(corpId), elasticSearchEntities);
                    weSensitiveService.hitSensitive1(corpId, elasticSearchEntities);
                    weAutoTagRuleHitKeywordRecordService.makeTagToNewCustomer(elasticSearchEntities, corpId);
                }
            } catch (Exception e) {
                log.error("会话存档执行异常, corpId:{}, ex:{}", corpId, ExceptionUtils.getStackTrace(e));
            }
        });
    }

    private void setRevokeInfo1(List<ChatInfoVO> chatDataList, String corpId) {
        // 理论上不存在msgid相同的情况，否则就是企业微信方的错误
        Map<String, ChatInfoVO> idMap = chatDataList.stream().collect(Collectors.toMap(ChatInfoVO::getMsgid, data -> data));
        chatDataList.forEach(data -> {
            //撤回标识
            String msgtype = data.getMsgtype();
            if (MsgTypeEnum.REVOKE.getType().equals(msgtype)) {
                String revokeMsgId = data.getRevoke().getPre_msgid();
                if (idMap.containsKey(revokeMsgId)) {
                    idMap.get(revokeMsgId).setIsRevoke(Boolean.TRUE);
                } else {
                    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.MSG_ID, revokeMsgId));
                    SearchSourceBuilder builder = new SearchSourceBuilder();
                    builder.query(boolQueryBuilder);
                    List<ChatInfoVO> search = elasticSearch.search(WeConstans.getChatDataIndex(corpId), builder, ChatInfoVO.class);
                    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(search)) {
                        search.get(0).setIsRevoke(Boolean.TRUE);
                        elasticSearch.updateBatchByJson(WeConstans.getChatDataIndex(corpId), search);
                    }
                }
            }
        });
    }

//    private void setRevokeInfo(List<JSONObject> msgList, Map<String, JSONObject> idMap, String corpId) {
//        msgList.forEach(jsonObject -> {
//            //撤回标识
//            String msgtype = jsonObject.getString("msgtype");
//            if ("revoke".equals(msgtype)) {
//                String revokeMsgId = jsonObject.getJSONObject("revoke").getString("pre_msgid");
//                if (idMap.containsKey(revokeMsgId)) {
//                    idMap.get(revokeMsgId).put("isRevoke", Boolean.TRUE);
//                } else {
//                    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.MSG_ID, revokeMsgId));
//                    SearchSourceBuilder builder = new SearchSourceBuilder();
//                    builder.query(boolQueryBuilder);
//                    List<JSONObject> search = elasticSearch.search(WeConstans.getChatDataIndex(corpId), builder, JSONObject.class);
//                    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(search)) {
//                        search.get(0).put("isRevoke", Boolean.TRUE);
//                        elasticSearch.updateBatchByJson(WeConstans.getChatDataIndex(corpId), search);
//                    }
//                }
//            }
//        });
//    }

    /**
     * 设置redis中的seq
     *
     * @param index  缓存中的sql
     * @param corpId 企业id
     */
    private void setRedisCacheSeqValue(AtomicLong index, String corpId) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SortBuilder<?> sortBuilderPrice = SortBuilders.fieldSort(WeConstans.CONTACT_SEQ_KEY).order(SortOrder.DESC);
        searchSourceBuilder.sort(sortBuilderPrice);
        searchSourceBuilder.size(1);
        List<JSONObject> searchResultList = elasticSearch.search(WeConstans.getChatDataIndex(corpId), searchSourceBuilder, JSONObject.class);
        if (CollUtil.isNotEmpty(searchResultList)) {
            searchResultList.stream().findFirst().ifPresent(result -> index.set(result.getLong(WeConstans.CONTACT_SEQ_KEY)));
            redisCache.setCacheObject(WeConstans.getContactSeqKey(corpId), index);
        }

    }


    /**
     * 扫描群发消息定时任务
     */
    public void messageTask() {
        //获的当前时间的毫秒数
        long currentTime = System.currentTimeMillis();
        //customerMessageTimeTaskMapper
        List<WeCustomerMessageTimeTask> weCustomerMessageTimeTasks = customerMessageTimeTaskMapper.selectWeCustomerMessageTimeTaskGteSettingTime(currentTime);

        final Semaphore semaphore = new Semaphore(5);

        if (CollectionUtils.isNotEmpty(weCustomerMessageTimeTasks)) {
            weCustomerMessageTimeTasks.forEach(
                    task -> {
                        try {
                            semaphore.acquire();
                            //校验入参 非空且发送类型正确才调用接口
                            boolean isCustomer = task.getMessageInfo().getPushType().equals(WeConstans.SEND_MESSAGE_CUSTOMER)
                                    && CollectionUtils.isNotEmpty(task.getCustomersInfo());
                            boolean isGroup = task.getMessageInfo().getPushType().equals(WeConstans.SEND_MESSAGE_GROUP)
                                    && CollectionUtils.isNotEmpty(task.getGroupsInfo());
                            boolean isNotNull = task.getMessageInfo() != null && task.getMessageId() != null;
                            if (isNotNull || isCustomer || isGroup) {
                                weCustomerMessageService.sendMessage(task.getMessageInfo(), task.getMessageId(), task.getCustomersInfo());
                            } else {
                                log.warn("群发定时任务传入参数错误 messageId:{} , messageInfo:{}, customerInfo:{},groupsInfo:{}",
                                        task.getMessageId(), task.getMessageInfo(), task.getCustomersInfo(), task.getGroupsInfo());
                            }
                        } catch (JsonProcessingException | InterruptedException | ForestRuntimeException e) {
                            log.error("定时群发消息处理异常：ex:{}", ExceptionUtils.getStackTrace(e));
                        } finally {
                            //更新消息处理状态
                            customerMessageTimeTaskMapper.updateTaskSolvedById(task.getTaskId());
                            semaphore.release();
                        }
                    }
            );
        }

    }

}
