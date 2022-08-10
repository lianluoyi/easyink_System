package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.GroupConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.ConversationArchiveQuery;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.conversation.msgtype.MsgTypeEnum;
import com.easyink.common.core.domain.conversation.msgtype.RevokeVO;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.elasticsearch.ElasticSearch;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import com.easyink.wecom.service.*;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
        BoolQueryBuilder fromBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, ""))
                .must(QueryBuilders.matchQuery(WeConstans.FROM, query.getFromId()))
                .must(QueryBuilders.matchQuery(WeConstans.TO_LIST, query.getReceiveId()));

        BoolQueryBuilder toLsitBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, ""))
                .must(QueryBuilders.matchQuery(WeConstans.FROM, query.getReceiveId()))
                .must(QueryBuilders.matchQuery(WeConstans.TO_LIST, query.getFromId()));
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

        BoolQueryBuilder fromBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, query.getRoomId()))
                .must(QueryBuilders.matchQuery("from", query.getFromId()));

        BoolQueryBuilder roomidBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, query.getRoomId()))
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
                .must(QueryBuilders.matchQuery(WeConstans.FROM, fromId))
                .must(QueryBuilders.matchQuery(WeConstans.TO_LIST, receiveId));

        BoolQueryBuilder toLsitBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, ""))
                .must(QueryBuilders.matchQuery(WeConstans.FROM, receiveId))
                .must(QueryBuilders.matchQuery(WeConstans.TO_LIST, fromId));
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
                .must(QueryBuilders.matchQuery(WeConstans.FROM, fromId));

        BoolQueryBuilder roomidBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(WeConstans.ROOMID, roomId))
                .must(QueryBuilders.matchQuery(WeConstans.TO_LIST, fromId));
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
        List<String> userIds = weUserService.listOfUserId(loginUser.getCorpId(), loginUser.getDepartmentDataScope().split(WeConstans.COMMA));
        //若为空则只能看本人的数据
        if (CollectionUtils.isEmpty(userIds)) {
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
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("fromInfo.name", "*" + query.getUserName() + "*"))
                    .mustNot(QueryBuilders.existsQuery("fromInfo.externalUserid")));
        }
        //客户姓名查询
        if (StringUtils.isNotEmpty(query.getCustomerName())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("fromInfo.name", "*" + query.getCustomerName() + "*"))
                    .must(QueryBuilders.existsQuery("fromInfo.externalUserid")));
        }

        //发送者姓名查询
        if (StringUtils.isNotEmpty(query.getFromName())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("fromInfo.name", "*" + query.getFromName() + "*")));
        }

        //接收者姓名查询
        if (StringUtils.isNotEmpty(query.getReceiveName())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("toListInfo.name", "*" + query.getReceiveName() + "*")));
        }

        //消息动作
        if (StringUtils.isNotEmpty(query.getAction())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("action", query.getAction())));
        }

        //关键词查询并高亮显示
        if (StringUtils.isNotEmpty(query.getKeyWord())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery("text.content", "*" + query.getKeyWord() + "*")));
            builder.highlighter(new HighlightBuilder().field("text.content"));
        }

        //时间范围查询
        if (StringUtils.isNotEmpty(query.getBeginTime()) && StringUtils.isNotEmpty(query.getEndTime())) {
            Date beginTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, query.getBeginTime());
            Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, query.getEndTime());
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).gte(beginTime.getTime()).lte(endTime.getTime()));
        }
        //权限过滤
        boolQueryBuilder.filter(QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("fromInfo.userId", userArray))
                .should(QueryBuilders.termsQuery("toListInfo.userId", userArray))
                .minimumShouldMatch(1));
        builder.query(boolQueryBuilder);
        PageInfo<ConversationArchiveVO> pageInfo = elasticSearch.searchPage(WeConstans.getChatDataIndex(query.getCorpId()), builder, pageNum, pageSize, ConversationArchiveVO.class);
        filterData(pageInfo, query.getCorpId());
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


    private void filterData(PageInfo<ConversationArchiveVO> pageList, String corpId) {
        if (StringUtils.isEmpty(corpId)) {
            log.error("corpId不能为空");
            return;
        }
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
                        WeCustomer weCustomer = weCustomerService.selectWeCustomerById(fromId, corpId);
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
                    WeCustomer weCustomer = weCustomerService.selectWeCustomerById(toUserId, corpId);
                    conversationArchiveVO.setToListInfo(JSON.parseObject(JSON.toJSONString(weCustomer)));
                }
            }
            // 撤回消息内容补充
            if (MsgTypeEnum.REVOKE.getType().equals(conversationArchiveVO.getMsgType())) {
                RevokeVO revoke = conversationArchiveVO.getRevoke();
                if (ObjectUtils.isEmpty(revoke) || org.apache.commons.lang3.StringUtils.isBlank(revoke.getPre_msgid())) {
                    continue;
                }
                SearchSourceBuilder builder = new SearchSourceBuilder();
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

}
