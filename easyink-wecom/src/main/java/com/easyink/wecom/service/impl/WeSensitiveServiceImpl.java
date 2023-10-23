package com.easyink.wecom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.GroupConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.conversation.ConversationArchiveConstants;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.elastic.ElasticSearchEntity;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.elasticsearch.ElasticSearch;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.MessageType;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeSensitive;
import com.easyink.wecom.domain.WeSensitiveAuditScope;
import com.easyink.wecom.domain.dto.WeGroupMemberDTO;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.query.WeSensitiveHitQuery;
import com.easyink.wecom.domain.vo.WeUserBriefInfoVO;
import com.easyink.wecom.domain.vo.WeUserVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeSensitiveMapper;
import com.easyink.wecom.service.*;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 敏感词设置Service业务层处理
 *
 * @author admin
 * @date 2020-12-29
 */
@Service
@Slf4j
public class WeSensitiveServiceImpl implements WeSensitiveService {
    @Autowired
    private WeSensitiveMapper weSensitiveMapper;

    @Autowired
    private WeSensitiveAuditScopeService sensitiveAuditScopeService;

    @Autowired
    private ElasticSearch elasticSearch;

    @Autowired
    private WeUserService weUserService;

    @Autowired
    private WeMessagePushClient weMessagePushClient;

    @Autowired
    private WeCorpAccountService weCorpAccountService;

    @Autowired
    private WeGroupService weGroupService;
    @Autowired
    private WeGroupMemberService weGroupMemberService;

    /**
     * 查询敏感词设置
     *
     * @param id 敏感词设置ID
     * @return 敏感词设置
     */
    @Override
    public WeSensitive selectWeSensitiveById(Long id) {
        return weSensitiveMapper.selectWeSensitiveById(id);
    }

    /**
     * 查询敏感词设置列表
     *
     * @param weSensitive 敏感词设置
     * @return 敏感词设置
     */
    @Override
    public List<WeSensitive> selectWeSensitiveList(WeSensitive weSensitive) {
        if (StringUtils.isBlank(weSensitive.getCorpId())) {
            return new ArrayList<>();
        }
        return weSensitiveMapper.selectWeSensitiveList(weSensitive);
    }

    /**
     * 新增敏感词设置
     *
     * @param weSensitive 敏感词设置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWeSensitive(WeSensitive weSensitive) {
        if (StringUtils.isBlank(weSensitive.getCorpId())) {
            throw new WeComException("企业ID不能为空");
        }
        weSensitive.setCreateBy(LoginTokenService.getUsername());
        weSensitive.setCreateTime(DateUtils.getNowDate());
        int insertResult = weSensitiveMapper.insertWeSensitive(weSensitive);
        if (insertResult > 0 && CollectionUtils.isNotEmpty(weSensitive.getAuditUserScope()) && weSensitive.getId() != null) {
            for (WeSensitiveAuditScope scope : weSensitive.getAuditUserScope()) {
                scope.setSensitiveId(weSensitive.getId());
                scope.setCorpId(weSensitive.getCorpId());
            }
            sensitiveAuditScopeService.insertWeSensitiveAuditScopeList(weSensitive.getAuditUserScope());
        }
        return insertResult;
    }

    /**
     * 修改敏感词设置
     *
     * @param weSensitive 敏感词设置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWeSensitive(WeSensitive weSensitive) {
        if (StringUtils.isBlank(weSensitive.getCorpId())) {
            throw new WeComException("企业ID不能为空");
        }
        weSensitive.setUpdateBy(LoginTokenService.getUsername());
        weSensitive.setUpdateTime(DateUtils.getNowDate());
        int updateResult = weSensitiveMapper.updateWeSensitive(weSensitive);
        if (updateResult > 0 && CollectionUtils.isNotEmpty(weSensitive.getAuditUserScope()) && weSensitive.getId() != null) {
            // 删除原有敏感词审计范围联系表数据
            sensitiveAuditScopeService.deleteAuditScopeBySensitiveId(weSensitive.getId());
            for (WeSensitiveAuditScope scope : weSensitive.getAuditUserScope()) {
                scope.setSensitiveId(weSensitive.getId());
                scope.setCorpId(weSensitive.getCorpId());
            }
            // 重新添加敏感词审计范围联系数据
            sensitiveAuditScopeService.insertWeSensitiveAuditScopeList(weSensitive.getAuditUserScope());
        }
        return updateResult;
    }

    /**
     * 批量删除敏感词设置
     *
     * @param ids 需要删除的敏感词设置ID
     * @return 结果
     */
    @Override
    public int deleteWeSensitiveByIds(Long[] ids) {
        List<WeSensitive> sensitiveList = weSensitiveMapper.selectWeSensitiveByIds(ids);
        sensitiveList.forEach(sensitive -> {
            sensitive.setDelFlag(1);
            sensitive.setUpdateBy(LoginTokenService.getUsername());
            sensitive.setUpdateTime(DateUtils.getNowDate());
        });
        return weSensitiveMapper.batchUpdateWeSensitive(sensitiveList);
    }

    /**
     * 批量删除敏感词配置数据
     *
     * @param ids 敏感词设置ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int destroyWeSensitiveByIds(Long[] ids) {
        int deleteResult = weSensitiveMapper.deleteWeSensitiveByIds(ids);
        if (deleteResult > 0) {
            //删除关联数据
            sensitiveAuditScopeService.deleteAuditScopeBySensitiveIds(ids);
        }
        return deleteResult;
    }

    @Override
    public PageInfo<JSONObject> getHitSensitiveList(WeSensitiveHitQuery weSensitiveHitQuery, LoginUser loginUser) {
        String corpId = loginUser.getCorpId();
        if (StringUtils.isBlank(corpId)) {
            return new PageInfo<>();
        }
        elasticSearch.createIndex2(WeConstans.getWecomSensitiveHitIndex(corpId), getSensitiveHitMapping());
        List<String> userIds = Lists.newArrayList();
        //权限下的员工
        List<String> weUsers;
        if (StringUtils.isBlank(loginUser.getDepartmentDataScope())) {
            weUsers = new ArrayList<>();
        } else {
            weUsers = weUserService.listOfUserId(loginUser.getCorpId(), loginUser.getDepartmentDataScope().split(WeConstans.COMMA));
        }
        //若为空则只能看本人的数据
        if (org.apache.commons.collections.CollectionUtils.isEmpty(weUsers)) {
            weUsers.add(loginUser.getUserId());
        }
        String[] userArray = weUsers.toArray(new String[0]);
        if (weSensitiveHitQuery.getScopeType().equals(WeConstans.USE_SCOP_BUSINESSID_TYPE_USER)) {
            if (org.apache.commons.lang3.StringUtils.isNoneBlank(weSensitiveHitQuery.getAuditScopeId())) {
                userIds.add(weSensitiveHitQuery.getAuditScopeId());
            }
        } else {
            List<String> userIdList = weUserService.selectWeUserList(WeUser.builder().department(new String[]{weSensitiveHitQuery.getAuditScopeId()}).build())
                    .stream().filter(Objects::nonNull).map(WeUser::getUserId).collect(Collectors.toList());
            userIds.addAll(userIdList);
        }
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize() == null ? 10 : pageDomain.getPageSize();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        int from = (pageNum - 1) * pageSize;
        builder.size(pageSize);
        builder.from(from);
        builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (CollectionUtils.isNotEmpty(userIds)) {
            BoolQueryBuilder userBuilder = QueryBuilders.boolQuery();
            for (String user : userIds) {
                userBuilder.should(QueryBuilders.termQuery(WeConstans.FROM, user));
            }

            userBuilder.minimumShouldMatch(1);
            boolQueryBuilder.must(userBuilder);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(weSensitiveHitQuery.getKeyword())) {
            BoolQueryBuilder keywordBuilder = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("text.content", weSensitiveHitQuery.getKeyword()));
            boolQueryBuilder.must(keywordBuilder);
        }
        //过滤权限下的数据
        boolQueryBuilder.should(QueryBuilders.termsQuery("from", userArray)).minimumShouldMatch(1);
        builder.query(boolQueryBuilder);
        PageInfo<JSONObject> pageInfo = elasticSearch.searchPage(WeConstans.getWecomSensitiveHitIndex(corpId), builder, pageNum, pageSize, JSONObject.class);
        return hitPageInfoHandler(pageInfo, corpId);
    }

    @Override
    public void hitSensitive1(String corpId, List<ChatInfoVO> entityList) {
        if (StringUtils.isBlank(corpId)) {
            log.info("执行敏感词命中过滤失败, corpId为空");
            return;
        }
        log.info("执行敏感词命中过滤, corpId={}, time=[{}]", corpId, System.currentTimeMillis());
        //只针对文本消息进行敏感词过滤
        List<ChatInfoVO> textList = new ArrayList<>();
        Optional<ChatInfoVO> textOp = entityList.stream().findFirst().filter(item -> {
            return "send".equals(item.getAction()) && "text".equals(item.getMsgtype());
        });
        if (!textOp.isPresent()) {
            return;
        }
//        for (ChatInfoVO obj : entityList) {
//            if ("send".equals(obj.getAction()) && "text".equals(obj.getMsgtype())) {
//                textList.add(obj);
//            }
//        }
//
//
//        if (CollectionUtils.isEmpty(textList)) {
//            log.info("没有需要匹配的新文本消息");
//            return;
//        }
        //此处休眠是因为前面写入chatdata的数据存在延迟
        try {
            Thread.sleep(3 * 1000L);
        } catch (InterruptedException e) {
            log.info("线程休眠异常:{}", ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
        }
        long minTimeStamp = 0L;
        for (ChatInfoVO jsonObject : entityList) {
            if (minTimeStamp == 0L) {
                minTimeStamp = jsonObject.getMsgtime();
                continue;
            }
            if (jsonObject.getMsgtime() < minTimeStamp) {
                minTimeStamp = jsonObject.getMsgtime();
            }
        }

        final Long timeStamp = minTimeStamp;
        //获取所有的敏感词规则
        WeSensitive weSensitive1 = new WeSensitive();
        weSensitive1.setCorpId(corpId);
        List<WeSensitive> allSensitiveRules = weSensitiveMapper.selectWeSensitiveList(weSensitive1);
        //根据规则过滤命中
        if (CollectionUtils.isEmpty(allSensitiveRules)) {
            return;
        }
        allSensitiveRules.forEach(weSensitive -> {
            List<JSONObject> jsonList = Lists.newArrayList();
            List<String> patternWords = Arrays.asList(weSensitive.getPatternWords().split(","));
            List<String> users = getScopeUsers(weSensitive.getAuditUserScope());
            if (CollectionUtils.isEmpty(patternWords) || CollectionUtils.isEmpty(users)) {
                return;
            }
            for (String patternWord : patternWords) {
                jsonList.addAll(hitSensitiveInES(patternWord, users, timeStamp, corpId));
            }
            //将命中结果插入es
            if (CollectionUtils.isEmpty(jsonList)) {
                return;
            }
            addHitSensitiveList(jsonList, weSensitive);
        });
    }

    private PageInfo<JSONObject> hitPageInfoHandler(PageInfo<JSONObject> pageInfo, String corpId) {
        List<JSONObject> jsonList = pageInfo.getList();
        if (CollectionUtils.isNotEmpty(jsonList)) {
            List<JSONObject> newList = jsonList.stream().map(j -> {
                JSONObject json = new JSONObject();
                String userId = j.getString(WeConstans.FROM);
                WeUserVO userVO = weUserService.getUser(corpId, userId);
                json.put(ConversationArchiveConstants.MSG_ID, j.get(WeConstans.MSG_ID));
                json.put(WeConstans.FROM, userVO.getUserName());
                json.put(WeConstans.CONTENT, j.getJSONObject(WeConstans.TEXT).getString(WeConstans.CONTENT));
                json.put(WeConstans.MSG_TIME, j.getString(WeConstans.MSG_TIME));
                json.put(WeConstans.STATUS, j.getString(WeConstans.STATUS));
                json.put(WeConstans.PATTERN_WORDS, j.getString(WeConstans.PATTERN_WORDS));
                json.put(WeConstans.FROMM_INFO, j.getJSONObject(WeConstans.FROMM_INFO));
                json.put(WeConstans.TO_LIST_INFO, j.getJSONObject(WeConstans.TO_LIST_INFO));
                if (org.apache.commons.lang3.StringUtils.isNotBlank(j.getString("roomid"))) {
                    String roomId = j.getString("roomid");
                    // 是群消息
                    WeGroup weGroup = weGroupService.getOne(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getChatId, roomId).last("limit 1"));
                    //查询群成员头像
                    if (weGroup != null) {
                        List<WeGroupMemberDTO> weGroupMemberDtos = weGroupMemberService.selectWeGroupMemberListByChatId(roomId);
                        String roomAvatar = weGroupMemberDtos.stream()
                                .map(WeGroupMemberDTO::getMemberAvatar)
                                .filter(com.easyink.common.utils.StringUtils::isNotEmpty)
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
                    json.put(WeConstans.TO_LIST_INFO, JSONObject.parseObject(JSONObject.toJSONString(weGroup)));
                }
                return json;
            }).collect(Collectors.toList());
            pageInfo.setList(newList);
        }
        return pageInfo;
    }

    private String getExtraChatName(List<WeGroupMemberDTO> memberLists) {
        StringBuilder chatName = new StringBuilder();
        int customerNum = 0;
        //备用群名拼接
        for (WeGroupMemberDTO member : memberLists) {
            //备用群名群成员添加
            int length = 3;
            if (customerNum < length) {
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

    private void addHitSensitiveList(List<JSONObject> json, WeSensitive weSensitive) {
        if (ObjectUtils.isEmpty(weSensitive) || StringUtils.isBlank(weSensitive.getCorpId()) || StringUtils.isBlank(weSensitive.getStrategyName())) {
            return;
        }
        log.info("命中敏感词策略:{},条数:{}", weSensitive.getStrategyName(), json.size());
        elasticSearch.createIndex2(WeConstans.getWecomSensitiveHitIndex(weSensitive.getCorpId()), getSensitiveHitMapping());
        //批量提交插入记录
        if (CollectionUtils.isNotEmpty(json)) {
            List<ElasticSearchEntity> list = json.stream().filter(Objects::nonNull).map(j -> {
                ElasticSearchEntity ese = new ElasticSearchEntity();
                j.put(WeConstans.STATUS, "0");
                ese.setData(j);
                ese.setId(j.getString(WeConstans.MSG_ID));
                return ese;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                elasticSearch.insertBatchEntity(WeConstans.getWecomSensitiveHitIndex(weSensitive.getCorpId()), list);
                sendMessage(list, weSensitive);
            }
        }
    }

    private void sendMessage(List<ElasticSearchEntity> list, WeSensitive weSensitive) {
        if (WeConstans.SENSITIVE_NOTICE.equals(weSensitive.getAlertFlag()) && CollectionUtils.isNotEmpty(list)) {
            //发送消息通知给相应的审计人
            //批量更新
            List<ElasticSearchEntity> result = new ArrayList<>();
            for (ElasticSearchEntity entity : list) {
                String name = "";
                String word = "";
                Object obj = entity.getData().get(WeConstans.FROMM_INFO);
                Object wordObj = entity.getData().get(WeConstans.PATTERN_WORDS);
                if (obj != null) {
                    JSONObject weUser = (JSONObject) obj;
                    name = weUser.getString("name");
                }
                if (wordObj != null) {
                    word = (String) wordObj;
                }
                WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount(weSensitive.getCorpId());
                String auditUserId = weSensitive.getAuditUserId();
                String content = "员工%s发送的消息包含敏感词“%s”，请进入easyink【会话存档】的消息审计查看完整消息";
                content = String.format(content, name, word);
                TextMessageDTO textMessageDTO = new TextMessageDTO();
                textMessageDTO.setContent(content);
                WeMessagePushDTO pushDto = new WeMessagePushDTO();
                pushDto.setTouser(auditUserId);
                pushDto.setMsgtype(MessageType.TEXT.getMessageType());
                pushDto.setText(textMessageDTO);
                pushDto.setAgentid(Integer.valueOf(weCorpAccount.getAgentId()));
                weMessagePushClient.sendMessageToUser(pushDto, weCorpAccount.getAgentId(), weSensitive.getCorpId());

                Map<String, String> map = entity.getData();
                map.put(WeConstans.STATUS, "1");
                entity.setData(map);
                result.add(entity);
            }
            list = result;
            elasticSearch.updateBatch(WeConstans.getWecomSensitiveHitIndex(weSensitive.getCorpId()), list);
        }
    }

    private List<String> getScopeUsers(List<WeSensitiveAuditScope> scopeList) {
        List<String> users = Lists.newArrayList();
        scopeList.forEach(scope -> {
            if (StringUtils.isBlank(scope.getCorpId()) || StringUtils.isBlank(scope.getAuditScopeId())) {
                return;
            }
            if (WeConstans.USE_SCOP_BUSINESSID_TYPE_ALL.equals(scope.getScopeType())) {
                List<WeUserBriefInfoVO> userBriefInfoList = weUserService.selectWeUserBriefInfo(WeUser.builder().corpId(scope.getCorpId()).build());
                if (CollectionUtils.isNotEmpty(userBriefInfoList)) {
                    List<String> userIdList = userBriefInfoList.stream().filter(Objects::nonNull).map(WeUserBriefInfoVO::getUserId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(userIdList)) {
                        users.addAll(userIdList);
                    }
                }
            } else if (WeConstans.USE_SCOP_BUSINESSID_TYPE_USER.equals(scope.getScopeType())) {
                users.add(scope.getAuditScopeId());
            } else {
                List<WeUserBriefInfoVO> userBriefInfoList = weUserService.selectWeUserBriefInfo(WeUser.builder().corpId(scope.getCorpId()).department(new String[]{scope.getAuditScopeId()}).build());
                if (CollectionUtils.isNotEmpty(userBriefInfoList)) {
                    List<String> userIdList = userBriefInfoList.stream().filter(Objects::nonNull).map(WeUserBriefInfoVO::getUserId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(userIdList)) {
                        users.addAll(userIdList);
                    }
                }
            }
        });
        return users;
    }

    private List<JSONObject> hitSensitiveInES(String patternWord, List<String> users, Long minTimeStamp, String corpId) {
        if (CollectionUtils.isNotEmpty(users) && StringUtils.isNoneBlank(patternWord) && StringUtils.isNotBlank(corpId)) {
            List<JSONObject> resultList = Lists.newArrayList();
            SearchSourceBuilder builder = new SearchSourceBuilder();
            builder.sort(WeConstans.MSG_TIME, SortOrder.DESC);
            BoolQueryBuilder userBuilder = QueryBuilders.boolQuery();
            userBuilder.should(QueryBuilders.termsQuery(WeConstans.FROM, users));
            userBuilder.minimumShouldMatch(1);

            BoolQueryBuilder timeBuilder = QueryBuilders.boolQuery();
            // 过滤已经匹配入库的
            timeBuilder.filter(QueryBuilders.rangeQuery(WeConstans.MSG_TIME).gte(minTimeStamp));
            BoolQueryBuilder searchBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.wildcardQuery("text.content.keyword", "*" + patternWord + "*"))
                    .must(userBuilder)
                    .must(timeBuilder);
            builder.query(searchBuilder);
            List<JSONObject> list = elasticSearch.search(WeConstans.getChatDataIndex(corpId), builder, JSONObject.class);
            if (CollectionUtils.isNotEmpty(list)) {
                list.parallelStream().forEach(j -> j.put(WeConstans.PATTERN_WORDS, patternWord));
                resultList.addAll(list);
            } else {
                log.info("patternWord：{},size:{},minTimeStamp:{}", patternWord, list.size(), minTimeStamp);
            }
            return resultList;
        }
        return Lists.newArrayList();
    }


    private XContentBuilder getSensitiveHitMapping() {
        String type = "type";
        String keyword = "keyword";
        String longs = "long";
        String text = "text";

        try {
            //创建索引
            return XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(WeConstans.PROPERTIES)
                    .startObject(WeConstans.MSG_ID)
                    .field(type, keyword)
                    .endObject()
                    .startObject(WeConstans.SEQ)
                    .field(type, longs)
                    .endObject()
                    .startObject(WeConstans.ACTION)
                    .field(type, keyword)
                    .endObject()
                    .startObject(WeConstans.FROM)
                    .field(type, keyword)
                    .endObject()
                    .startObject(WeConstans.ROOMID)
                    .field(type, keyword)
                    .endObject()
                    .startObject(WeConstans.MSG_TIME)
                    .field(type, longs)
                    .endObject()
                    .startObject(WeConstans.MSG_TYPE)
                    .field(type, keyword)
                    .endObject()
                    .startObject(WeConstans.STATUS)
                    .field(type, keyword)
                    .endObject()
                    .startObject(WeConstans.PATTERN_WORDS)
                    .field(type, keyword)
                    .endObject()
                    .startObject(WeConstans.CONTENT)
                    .field(type, text)
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (Exception e) {
            log.warn("create sensitive-hit mapping failed, exception={}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}
