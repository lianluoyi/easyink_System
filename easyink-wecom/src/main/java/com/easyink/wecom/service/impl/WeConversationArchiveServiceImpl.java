package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.GroupConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.conversation.ConversationArchiveConstants;
import com.easyink.common.core.domain.ConversationArchiveQuery;
import com.easyink.common.core.domain.ConversationArchiveViewContextDTO;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.conversation.msgtype.MsgTypeEnum;
import com.easyink.common.core.domain.conversation.msgtype.RevokeVO;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.elasticsearch.ElasticSearch;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.idmapping.WeUserIdMappingService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description 会话存档业务实现类
 * @date 2020/12/19 14:00
 **/
@Slf4j
@Service
public class WeConversationArchiveServiceImpl implements WeConversationArchiveService {
    @Autowired
    private ElasticSearch elasticSearch;

    @Autowired
    private WeUserService weUserService;
    @Autowired
    private WeCustomerService weCustomerService;

    @Autowired
    private WeGroupService weGroupService;
    @Autowired
    private WeGroupMemberService weGroupMemberService;
    @Autowired
    private WeUserIdMappingService weUserIdMappingService ;
    @Autowired
    private RuoYiConfig ruoyiConfig ;

    private static final int LENGTH = 3;

    /**
     * 根据用户ID 获取对应内部联系人列表
     *
     * @param query 入参
     * @return
     */
    @Override
    public PageInfo<ConversationArchiveVO> getChatContactList(ConversationArchiveQuery query) {
        if (ObjectUtils.isEmpty(query) || StringUtils.isEmpty(query.getFromId()) || StringUtils.isEmpty(query.getReceiveId()) || StringUtils.isEmpty(query.getCorpId())) {
            return new PageInfo<ConversationArchiveVO>();
        }
        log.info("查询单聊数据:from:{},rec:{},corpId:{}", query.getFromId(), query.getReceiveId(), query.getCorpId());
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize() == null ? 10 : pageDomain.getPageSize();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        int from = (pageNum - 1) * pageSize;
        builder.size(pageSize);
        builder.from(from);
        builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);
        // 需要完全匹配
        BoolQueryBuilder fromBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, ""))
                .must(QueryBuilders.termsQuery(WeConstans.FROM, query.getFromId()))
                .must(QueryBuilders.termsQuery(WeConstans.TO_LIST_KEYWORD, query.getReceiveId()));

        BoolQueryBuilder toLsitBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, ""))
                .must(QueryBuilders.termsQuery(WeConstans.FROM, query.getReceiveId()))
                .must(QueryBuilders.termsQuery(WeConstans.TO_LIST_KEYWORD, query.getFromId()));
        //查询聊天类型
        if (StringUtils.isNotEmpty(query.getMsgType())) {
            fromBuilder.must(QueryBuilders.termQuery(WeConstans.MSG_TYPE, query.getMsgType()));
            toLsitBuilder.must(QueryBuilders.termQuery(WeConstans.MSG_TYPE, query.getMsgType()));
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(fromBuilder)
                .should(toLsitBuilder)
                .minimumShouldMatch(1);
        //时间范围查询
        if (StringUtils.isNotEmpty(query.getBeginTime()) && StringUtils.isNotEmpty(query.getEndTime())) {
            Date beginTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, query.getBeginTime());
            Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, query.getEndTime());
            Calendar c = Calendar.getInstance();
            c.setTime(endTime);
            c.add(Calendar.DAY_OF_MONTH, 1);
            endTime = c.getTime();
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).gte(beginTime.getTime()).lte(endTime.getTime()));
        }
        builder.query(boolQueryBuilder);
        PageInfo<ConversationArchiveVO> pageList = elasticSearch.searchPage(WeConstans.getChatDataIndex(query.getCorpId()), builder, pageNum, pageSize, ConversationArchiveVO.class);
        filterData(pageList, query.getCorpId());
        return pageList;
    }

    @Override
    public PageInfo<ConversationArchiveVO> getChatRoomContactList(ConversationArchiveQuery query) {
        if (ObjectUtils.isEmpty(query) || StringUtils.isEmpty(query.getRoomId()) || StringUtils.isEmpty(query.getCorpId())) {
            return new PageInfo<ConversationArchiveVO>();
        }
        log.info("查询群聊数据:roomId:{},corpId:{}", query.getRoomId(), query.getCorpId());
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize() == null ? 10 : pageDomain.getPageSize();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        int from = (pageNum - 1) * pageSize;
        builder.size(pageSize);
        builder.from(from);
        builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);

        BoolQueryBuilder fromBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(WeConstans.ROOMID, query.getRoomId()))
                .must(QueryBuilders.termsQuery("from", query.getFromId()));

        BoolQueryBuilder roomidBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(WeConstans.ROOMID, query.getRoomId()))
                .must(QueryBuilders.matchQuery(WeConstans.TO_LIST, query.getFromId()));

        //查询聊天类型
        if (StringUtils.isNotEmpty(query.getMsgType())) {
            fromBuilder.must(QueryBuilders.termQuery(WeConstans.MSG_TYPE, query.getMsgType()));
            roomidBuilder.must(QueryBuilders.termQuery(WeConstans.MSG_TYPE, query.getMsgType()));
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(fromBuilder)
                .should(roomidBuilder)
                .minimumShouldMatch(1);

        //时间范围查询
        if (StringUtils.isNotEmpty(query.getBeginTime()) && StringUtils.isNotEmpty(query.getEndTime())) {
            Date beginTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, query.getBeginTime());
            Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, query.getEndTime());
            Calendar c = Calendar.getInstance();
            c.setTime(endTime);
            c.add(Calendar.DAY_OF_MONTH, 1);
            endTime = c.getTime();
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).gte(beginTime.getTime()).lte(endTime.getTime()));
        }

        builder.query(boolQueryBuilder);
        return elasticSearch.searchPage(WeConstans.getChatDataIndex(query.getCorpId()), builder, pageNum, pageSize, ConversationArchiveVO.class);
    }


    @Override
    public JSONObject getFinalChatContactInfo(String fromId, String receiveId, String corpId) {
        log.info("查询单聊最后一条数据数据:receiveId:{},from:{},corpId:{}", receiveId, fromId, corpId);
        if (StringUtils.isEmpty(fromId) || StringUtils.isEmpty(receiveId) || StringUtils.isEmpty(corpId)) {
            return null;
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);
        BoolQueryBuilder fromBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, ""))
                .must(QueryBuilders.termsQuery(WeConstans.FROM, fromId))
                .must(QueryBuilders.termsQuery(WeConstans.TO_LIST_KEYWORD, receiveId));

        BoolQueryBuilder toLsitBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, ""))
                .must(QueryBuilders.termsQuery(WeConstans.FROM, receiveId))
                .must(QueryBuilders.termsQuery(WeConstans.TO_LIST_KEYWORD, fromId));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(fromBuilder)
                .should(toLsitBuilder)
                .minimumShouldMatch(1);
        builder.query(boolQueryBuilder);
        List<JSONObject> resultList = elasticSearch.search(WeConstans.getChatDataIndex(corpId), builder, JSONObject.class);
        if (CollUtil.isNotEmpty(resultList)) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public JSONObject getFinalChatRoomContactInfo(String fromId, String roomId, String corpId) {
        log.info("查询群聊最后一条数据:roomid:{},from:{},corpId:{}", roomId, fromId, corpId);
        if (StringUtils.isEmpty(fromId) || StringUtils.isEmpty(roomId) || StringUtils.isEmpty(corpId)) {
            return null;
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);
        BoolQueryBuilder fromBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, roomId))
                .must(QueryBuilders.termsQuery(WeConstans.FROM, fromId));

        BoolQueryBuilder roomidBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, roomId))
                .must(QueryBuilders.termsQuery(WeConstans.TO_LIST_KEYWORD, fromId));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(fromBuilder)
                .should(roomidBuilder);
        builder.query(boolQueryBuilder);
        List<JSONObject> resultList = elasticSearch.search(WeConstans.getChatDataIndex(corpId), builder, JSONObject.class);
        if (CollUtil.isNotEmpty(resultList)) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public PageInfo<ConversationArchiveVO> getChatAllList(ConversationArchiveQuery query, LoginUser loginUser) {
        if (ObjectUtils.isEmpty(query) || StringUtils.isEmpty(query.getCorpId())) {
            return null;
        }
        //权限下的员工
        List<String> userIds = weUserService.listOfUserId(loginUser.getCorpId(), loginUser.getDepartmentDataScope()
                                                                                          .split(WeConstans.COMMA));
        //若为空则只能看本人的数据 (admin不存在会话存档)
        if (CollectionUtils.isEmpty(userIds) && !loginUser.isSuperAdmin()) {
            userIds.add(loginUser.getWeUser().getUserId());
        }
        String[] userArray = userIds.toArray(new String[0]);
        log.info("查询全部数据:roomid:{},from:{},rec:{},corpId:{}", query.getRoomId(), query.getFromId(), query.getReceiveId(), query.getCorpId());
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize() == null ? 10 : pageDomain.getPageSize();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        int from = (pageNum - 1) * pageSize;
        builder.size(pageSize);
        builder.from(from);
        builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(WeConstans.MSG_TYPE, "agree"))
                .mustNot(QueryBuilders.termQuery(WeConstans.MSG_TYPE, "disagree"));


        //成员姓名查询
        if (StringUtils.isNotEmpty(query.getUserName())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("fromInfo.name.keyword", "*" + query.getUserName() + "*"))
                    .mustNot(QueryBuilders.existsQuery("fromInfo.externalUserid")));
        }
        //客户姓名查询
        if (StringUtils.isNotEmpty(query.getCustomerName())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("fromInfo.name.keyword", "*" + query.getCustomerName() + "*"))
                    .must(QueryBuilders.existsQuery("fromInfo.externalUserid")));
        }

        //发送者姓名查询
        if (StringUtils.isNotEmpty(query.getFromName())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("fromInfo.name.keyword", "*" + query.getFromName() + "*")));
        }

        //接收者姓名查询
        if (StringUtils.isNotEmpty(query.getReceiveName())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("toListInfo.name.keyword", "*" + query.getReceiveName() + "*")));
        }

        //消息动作
        if (StringUtils.isNotEmpty(query.getAction())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("action", query.getAction())));
        }

        //关键词查询并高亮显示
        if (StringUtils.isNotEmpty(query.getKeyWord())) {
            // 查询为全部状态查询
            if (StringUtils.isBlank(query.getAction())) {
                // 将查询条件改为全部查询
                builder.from(ConversationArchiveConstants.SEARCH_FROM);
                builder.size(ConversationArchiveConstants.SEARCH_SIZE);
            }
            // 撤回类型消息特殊处理
            if (ConversationArchiveConstants.ACTION_RECALL.equals(query.getAction())) {
                // 将查询条件改为全部查询
                builder.from(ConversationArchiveConstants.SEARCH_FROM);
                builder.size(ConversationArchiveConstants.SEARCH_SIZE);
            } else {
                boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("text.content.keyword", "*" + query.getKeyWord() + "*")));
                builder.highlighter(new HighlightBuilder().field("text.content"));
            }
        }

        //时间范围查询
        if (StringUtils.isNotEmpty(query.getBeginTime()) && StringUtils.isNotEmpty(query.getEndTime())) {
            Date beginTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, query.getBeginTime());
            Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, query.getEndTime());
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).gte(beginTime.getTime()).lte(endTime.getTime()));
        }
        //权限过滤
        //匹配发送人
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("from", userArray));
        //匹配收消息人
        for (String userId : userArray) {
            queryBuilder.should(QueryBuilders.matchQuery("toListInfo.userId", userId));
        }
        boolQueryBuilder.filter(queryBuilder.minimumShouldMatch(1));
        builder.query(boolQueryBuilder);
        PageInfo<ConversationArchiveVO> pageInfo = elasticSearch.searchPage(WeConstans.getChatDataIndex(query.getCorpId()), builder, pageNum, pageSize, ConversationArchiveVO.class);
        filterData(pageInfo, query.getCorpId(), query.getKeyWord(), query.getAction());
        List<ConversationArchiveVO> list = pageInfo.getList();
        List<ConversationArchiveVO> conversationArchiveVOList = new ArrayList<>();
        for (ConversationArchiveVO conversationArchiveVO : list) {
            // 目前全局检索只显示员工发送的群消息
            if (CollectionUtils.isNotEmpty(conversationArchiveVO.getToList()) && conversationArchiveVO.getToList().size() > 1) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(conversationArchiveVO.getRoomId())) {
                    String roomId = conversationArchiveVO.getRoomId();
                    // 是群消息
                    WeGroup weGroup = weGroupService.getOne(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getChatId, roomId).last("limit 1"));
                    //查询群成员头像
                    if (weGroup != null) {
                        List<WeGroupMemberDTO> weGroupMemberDtos = weGroupMemberService.selectWeGroupMemberListByChatId(roomId);
                        String roomAvatar = weGroupMemberDtos.stream()
                                .map(WeGroupMemberDTO::getMemberAvatar)
                                .filter(StringUtils::isNotEmpty)
                                .limit(9)
                                .collect(Collectors.joining(","));
                        weGroup.setAvatar(roomAvatar);
                        if (org.apache.commons.lang3.StringUtils.isBlank(weGroup.getGroupName())) {
                            weGroup.setGroupName(getExtraChatName(weGroupMemberDtos));
                        }
                    } else {
                        // 查不到群信息 头像前端展示默认头像
                        weGroup = new WeGroup();
                        weGroup.setGroupName(GroupConstants.UNKNOW_GROUP_NAME);
                    }
                    conversationArchiveVO.setToListInfo(JSONObject.parseObject(JSONObject.toJSONString(weGroup)));
                }
            }
            conversationArchiveVOList.add(conversationArchiveVO);
        }
        if (CollectionUtils.isNotEmpty(conversationArchiveVOList)) {
            pageInfo.setList(conversationArchiveVOList);
        }
        return pageInfo;
    }

    @Override
    public PageInfo<ConversationArchiveVO> getChatList(ConversationArchiveQuery query, Integer pageNum, Integer pageSize) {
        if (ObjectUtils.isEmpty(query) || StringUtils.isEmpty(query.getCorpId())) {
            return null;
        }
        log.info("查询员工:{}的会话数据数据: roomid:{},corpId:{}", query.getFromId(), query.getRoomId(), query.getCorpId());
        SearchSourceBuilder builder = new SearchSourceBuilder();
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        int from = (pageNum - 1) * pageSize;
        builder.size(pageSize);
        builder.from(from);
        builder.sort(WeConstans.MSG_TIME, SortOrder.ASC);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(WeConstans.MSG_TYPE, "agree"))
                .mustNot(QueryBuilders.termQuery(WeConstans.MSG_TYPE, "disagree"));
        //时间范围查询
        if (StringUtils.isNotEmpty(query.getBeginTime()) && StringUtils.isNotEmpty(query.getEndTime())) {
            Date beginTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, query.getBeginTime());
            Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, query.getEndTime());
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).gte(beginTime.getTime()).lte(endTime.getTime()));
        }
        //匹配发送人, 需要100 %匹配
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("from", query.getFromId()));
        //匹配收消息人
        queryBuilder.should(QueryBuilders.termsQuery(WeConstans.TO_LIST_KEYWORD, query.getFromId()));
        boolQueryBuilder.filter(queryBuilder.minimumShouldMatch(1));
        builder.query(boolQueryBuilder);
        return elasticSearch.searchPage(WeConstans.getChatDataIndex(query.getCorpId()), builder, pageNum, pageSize, ConversationArchiveVO.class);
    }

    /**
     * 获取指定聊天内容的上下文信息
     *
     * @param dto {@link ConversationArchiveViewContextDTO}
     * @return {@link PageInfo<ConversationArchiveVO>}
     */
    @Override
    public TableDataInfo<ConversationArchiveVO> viewContext(ConversationArchiveViewContextDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            return null;
        }
        // 消息id
        String msgId = dto.getMsgId();
        // ES索引
        String index = WeConstans.getChatDataIndex(dto.getCorpId());
        // 发送者id
        String fromId = dto.getFromId();
        // 接收者id
        String receiveId = dto.getReceiveId();
        // 群聊id
        String roomId = dto.getRoomId();
        // 查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 返回的信息
        List<ConversationArchiveVO> resultList = new ArrayList<>();
        // 满足匹配消息id条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.MSG_ID, msgId));
        builder.query(boolQueryBuilder);
        // 当前消息的详细内容
        List<ConversationArchiveVO> search = elasticSearch.search(index, builder, ConversationArchiveVO.class);
        if (CollectionUtils.isEmpty(search)) {
            return null;
        }
        Long msgTime = search.get(0).getMsgTime();
        if (StringUtils.isBlank(dto.getType())) {
            // 将本身这条加进返回列表
            resultList.addAll(search);
            // 默认信息上文查询
            SearchSourceBuilder priorBuilder = getPriorContextBuilder(msgTime, ConversationArchiveConstants.DEFAULT_CONTEXT_NUM, fromId, receiveId, roomId);
            List<ConversationArchiveVO> defaultPriorList = elasticSearch.search(index, priorBuilder, ConversationArchiveVO.class);
            // 默认信息下文查询
            SearchSourceBuilder nextBuilder = getNextContextBuilder(msgTime, ConversationArchiveConstants.DEFAULT_CONTEXT_NUM, fromId, receiveId, roomId);
            List<ConversationArchiveVO> defaultNextList = elasticSearch.search(index, nextBuilder, ConversationArchiveVO.class);
            resultList.addAll(defaultPriorList);
            resultList.addAll(defaultNextList);
        }
        if (ConversationArchiveConstants.NEXT_CONTEXT.equals(dto.getType())) {
            SearchSourceBuilder priorBuilder = getPriorContextBuilder(msgTime, ConversationArchiveConstants.PAGE_CONTEXT_NUM, fromId, receiveId, roomId);
            List<ConversationArchiveVO> priorList = elasticSearch.search(index, priorBuilder, ConversationArchiveVO.class);
            resultList.addAll(priorList);
        }
        if (ConversationArchiveConstants.PRIOR_CONTEXT.equals(dto.getType())) {
            SearchSourceBuilder nextBuilder = getNextContextBuilder(msgTime, ConversationArchiveConstants.PAGE_CONTEXT_NUM, fromId, receiveId, roomId);
            List<ConversationArchiveVO> nextList = elasticSearch.search(index, nextBuilder, ConversationArchiveVO.class);
            resultList.addAll(nextList);
        }
        // 按照发送时间正序排序
        resultList.sort(Comparator.comparing(ConversationArchiveVO::getMsgTime));
        // 补充撤回消息内容
        supplyRecallMsg(resultList, dto.getCorpId());
        return PageInfoUtil.getDataTable(resultList);
    }

    /**
     * 获取上文信息查询条件
     *
     * @param msgTime   消息发送时间
     * @param size      查询消息条数
     * @param fromId    发送者id
     * @param receiveId 接收者id
     * @param roomId    群聊id
     * @return {@link SearchSourceBuilder}
     */
    private SearchSourceBuilder getPriorContextBuilder(Long msgTime, int size, String fromId, String receiveId, String roomId) {
        if (msgTime == null) {
            return null;
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder priorBuilder;
        builder.from(0);
        builder.size(size);
        // 查询上文数据
        priorBuilder = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).lt(msgTime));
        // 匹配发送人和接收人信息
        matchFromAndReceiveBuilder(priorBuilder, fromId, receiveId, roomId);
        builder.query(priorBuilder);
        builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);
        return builder;
    }

    /**
     * 获取下文信息查询条件
     *
     * @param msgTime   消息发送时间
     * @param size      查询消息条数
     * @param fromId    发送者id
     * @param receiveId 接收者id
     * @param roomId    群聊id
     * @return {@link SearchSourceBuilder}
     */
    private SearchSourceBuilder getNextContextBuilder(Long msgTime, int size, String fromId, String receiveId, String roomId) {
        if (msgTime == null) {
            return null;
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder nextBuilder;
        builder.from(0);
        builder.size(size);
        // 查询下文数据
        nextBuilder = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).gt(msgTime));
        // 匹配发送人和接收人信息
        matchFromAndReceiveBuilder(nextBuilder, fromId, receiveId, roomId);
        builder.query(nextBuilder);
        builder.sort(WeConstans.MSG_TIME, SortOrder.ASC);
        return builder;
    }

    /**
     * 匹配发送人和接收人信息或群聊信息
     *
     * @param queryBuilder {@link BoolQueryBuilder}
     * @param fromId       发送人id
     * @param receiveId    接收人id
     * @param roomId       群聊id
     */
    private void matchFromAndReceiveBuilder(BoolQueryBuilder queryBuilder, String fromId, String receiveId, String roomId) {
        if (queryBuilder == null) {
            return;
        }
        if (StringUtils.isNotBlank(fromId) && StringUtils.isNotBlank(receiveId)) {
            // 发送人匹配
            queryBuilder.should(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, StringUtils.EMPTY))
                    .must(QueryBuilders.matchQuery(WeConstans.FROM, fromId))
                    .must(QueryBuilders.matchQuery(WeConstans.TO_LIST_KEYWORD, receiveId)));
            // 接收人匹配
            queryBuilder.should(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, StringUtils.EMPTY))
                    .must(QueryBuilders.matchQuery(WeConstans.FROM, receiveId))
                    .must(QueryBuilders.matchQuery(WeConstans.TO_LIST_KEYWORD, fromId)));
        }
        if (StringUtils.isNotBlank(roomId)) {
            // 群聊匹配
            queryBuilder.should(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(WeConstans.ROOMID, roomId))
                    .must(QueryBuilders.termsQuery(WeConstans.FROM, fromId)));
            queryBuilder.should(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(WeConstans.ROOMID, roomId))
                    .must(QueryBuilders.matchQuery(WeConstans.TO_LIST, fromId)));
        }
        queryBuilder.minimumShouldMatch(1);
    }

    private String getExtraChatName(List<WeGroupMemberDTO> memberLists) {
        StringBuilder chatName = new StringBuilder();
        int customerNum = 0;
        //备用群名拼接
        for (WeGroupMemberDTO member : memberLists) {
            //备用群名群成员添加
            if (customerNum < LENGTH) {
                chatName.append(member.getMemberName()).append(WeConstans.COMMA);
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
     * 处理撤回状态消息，补偿消息内容
     *
     * @param pageList pageList {@link PageInfo<ConversationArchiveVO>}
     * @param corpId   企业ID
     */
    private void filterData(PageInfo<ConversationArchiveVO> pageList, String corpId) {
        filterData(pageList, corpId, null, null);
    }

    /**
     * 处理撤回状态消息，补偿消息内容
     *
     * @param pageList {@link PageInfo<ConversationArchiveVO>}
     * @param corpId   企业ID
     * @param keyWords 查询匹配内容
     * @param action   消息类型 send:已发送 recall:已撤回 null:全部查询
     */
    private void filterData(PageInfo<ConversationArchiveVO> pageList, String corpId, String keyWords, String action) {
        if (StringUtils.isEmpty(corpId)) {
            log.error("corpId不能为空");
            return;
        }
        // 全部查询处理
        searchAllMatchKeyWords(keyWords, pageList, corpId, action);
        for (ConversationArchiveVO conversationArchiveVO : pageList.getList()) {
            if (conversationArchiveVO.getFromInfo() == null) {
                String fromId = conversationArchiveVO.getFrom();
                if (conversationArchiveVO.getToListInfo() == null) {
                    continue;
                }
                if (org.apache.commons.lang3.StringUtils.isNoneBlank(fromId)) {
                    //少了员工信息
                    if (conversationArchiveVO.getToListInfo().containsKey("externalUserid")) {
                        WeUser user = weUserService.getById(fromId);
                        conversationArchiveVO.setFromInfo(JSON.parseObject(JSON.toJSONString(user)));
                    } else {
                        WeCustomer weCustomer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getExternalUserid, fromId).eq(WeCustomer::getCorpId, corpId).last(GenConstants.LIMIT_1));
                        conversationArchiveVO.setFromInfo(JSON.parseObject(JSON.toJSONString(weCustomer)));
                    }
                }
            } else if (conversationArchiveVO.getToListInfo() == null) {
                if (CollUtil.isEmpty(conversationArchiveVO.getToList()) || conversationArchiveVO.getFromInfo() == null) {
                    continue;
                }
                if (conversationArchiveVO.getFromInfo().containsKey("externalUserid")) {
                    String toUserId = conversationArchiveVO.getToList().get(0);
                    WeUser user = weUserService.getById(toUserId);
                    conversationArchiveVO.setToListInfo(JSON.parseObject(JSON.toJSONString(user)));
                } else {
                    String toUserId = conversationArchiveVO.getToList().get(0);
                    WeCustomer weCustomer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getExternalUserid, toUserId).eq(WeCustomer::getCorpId, corpId).last(GenConstants.LIMIT_1));
                    conversationArchiveVO.setToListInfo(JSON.parseObject(JSON.toJSONString(weCustomer)));
                }
            }
        }
        // 撤回消息内容补充
        supplyRecallMsg(pageList.getList(), corpId);
        // 处理撤回状态关键词查询数据
        recallDataFilter(keyWords, action, pageList);
    }

    /**
     * 补充撤回消息
     *
     * @param pageList {@link ConversationArchiveVO}
     * @param corpId   企业ID
     */
    private void supplyRecallMsg(List<ConversationArchiveVO> pageList, String corpId) {
        if (CollectionUtils.isEmpty(pageList) || StringUtils.isBlank(corpId)) {
            return;
        }
        for (ConversationArchiveVO conversationArchiveVO : pageList) {
            // 撤回消息内容补充
            if (MsgTypeEnum.REVOKE.getType().equals(conversationArchiveVO.getMsgType())) {
                RevokeVO revoke = conversationArchiveVO.getRevoke();
                SearchSourceBuilder builder = new SearchSourceBuilder();
                if (ObjectUtils.isEmpty(revoke) || org.apache.commons.lang3.StringUtils.isBlank(revoke.getPre_msgid())) {
                    continue;
                }
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.MSG_ID, revoke.getPre_msgid()));
                builder.query(boolQueryBuilder);
                List<ChatInfoVO> search = elasticSearch.search(WeConstans.getChatDataIndex(corpId), builder, ChatInfoVO.class);
                if (!CollectionUtils.isEmpty(search)) {
                    // 保存撤回消息内容
                    revoke.setContent(search.get(0));
                }
            }
        }
    }

    /**
     * 处理全部状态关键词查询（仅action为null时处理）
     *
     * @param keyWords 查询匹配内容
     * @param pageList {@link PageInfo<ConversationArchiveVO>}
     * @param corpId   企业ID
     * @param action   action 消息类型 send:已发送 recall:已撤回 null:全部查询
     */
    private void searchAllMatchKeyWords(String keyWords, PageInfo<ConversationArchiveVO> pageList, String corpId, String action) {
        if (StringUtils.isAnyBlank(keyWords, corpId) || pageList == null || StringUtils.isNotBlank(action)) {
            return;
        }
        List<String> msgIdList = new ArrayList<>();
        Iterator<ConversationArchiveVO> iterator = pageList.getList().iterator();
        while (iterator.hasNext()) {
            ConversationArchiveVO conversationArchiveVO = iterator.next();
            if (conversationArchiveVO.getIsRevoke() != null && conversationArchiveVO.getIsRevoke()) {
                msgIdList.add(conversationArchiveVO.getMsgId());
            }
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 全部查询
        builder.from(ConversationArchiveConstants.SEARCH_FROM);
        builder.size(ConversationArchiveConstants.SEARCH_SIZE);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery(WeConstans.PRE_MSG_ID, msgIdList.toArray(new String[0])));
        builder.query(queryBuilder);
        List<ConversationArchiveVO> search = elasticSearch.search(WeConstans.getChatDataIndex(corpId), builder, ConversationArchiveVO.class);
        pageList.getList().addAll(search);
        // 根据时间倒序
        pageList.getList().sort(Comparator.comparing(ConversationArchiveVO::getMsgTime).reversed());
        // 处理分页数据
        paging(pageList);
    }

    /**
     * 处理撤回状态关键词查询数据
     *
     * @param keyWords 查询匹配内容
     * @param action   action 消息类型 send:已发送 recall:已撤回 null:全部查询
     * @param pageList {@link PageInfo<ConversationArchiveVO>}
     */
    private void recallDataFilter(String keyWords, String action, PageInfo<ConversationArchiveVO> pageList) {
        if (StringUtils.isAnyBlank(keyWords, action) || pageList == null) {
            log.info("[会话存档全局检索] 处理撤回关键词查询异常，参数缺失：keyWords:{}, action:{}, pageList:{}", keyWords, action, pageList);
            return;
        }
        // 仅发送状态为recall时才处理
        if (ConversationArchiveConstants.ACTION_RECALL.equals(action)) {
            Iterator<ConversationArchiveVO> iterator = pageList.getList().iterator();
            while (iterator.hasNext()) {
                ConversationArchiveVO conversationArchiveVO = iterator.next();
                // 撤回信息内容为空，则删除
                if (conversationArchiveVO.getRevoke() == null
                        || conversationArchiveVO.getRevoke().getContent() == null
                        || conversationArchiveVO.getRevoke().getContent().getMsgtype() == null) {
                    iterator.remove();
                    continue;
                }
                // 类型不为撤回，或msgType不是text类型
                if (!MsgTypeEnum.TEXT.getType().equals(conversationArchiveVO.getRevoke().getContent().getMsgtype())) {
                    iterator.remove();
                    // 已删除就跳过当前循环
                    continue;
                }
                if (conversationArchiveVO.getRevoke().getContent().getText() != null
                        && conversationArchiveVO.getRevoke().getContent().getText().getContent() != null) {
                    String msgContent = conversationArchiveVO.getRevoke().getContent().getText().getContent();
                    // 过滤掉不匹配聊天内容关键词的内容
                    if (!msgContent.contains(keyWords)) {
                        iterator.remove();
                    }
                }
            }
            // 处理分页数据
            paging(pageList);
        }
    }

    /**
     * 处理分页参数
     *
     * @param pageList {@link PageInfo<ConversationArchiveVO>}
     */
    private void paging(PageInfo<ConversationArchiveVO> pageList) {
        int total = pageList.getList().size();
        int pages = total;
        if (total != 0 && total % pageList.getPageNum() == 0) {
            pages = total / pageList.getPageNum();
        } else if (total != 0 && total % pageList.getPageNum() != 0) {
            pages = (total / pageList.getPageNum()) + 1;
        }
        pageList.setTotal(total);
        pageList.setPages(pages);
        pageList.setHasNextPage(pageList.getPageNum() < pageList.getPages());
        // 手动截取分页
        int start = (pageList.getPageNum() - 1) * pageList.getPageSize();
        int end = Math.min(start + pageList.getPageSize(), total);
        pageList.setList(pageList.getList().subList(start, end));
    }

}
