package com.easyink.wecom.handler.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.conversation.msgtype.*;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.elasticsearch.ElasticSearch;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.client.WeUpdateIDClient;
import com.easyink.wecom.domain.entity.WeExternalUseridMapping;
import com.easyink.wecom.domain.entity.WeUserIdMapping;
import com.easyink.wecom.domain.enums.third.HaiderbaoMsgTypeEnum;
import com.easyink.wecom.domain.model.third.ImportSessionArchiveModel;
import com.easyink.wecom.openapi.dao.LockSelfBuildConfigMapper;
import com.easyink.wecom.openapi.domain.entity.LockSelfBuildConfig;
import com.easyink.wecom.openapi.model.DecryptExternalUserIdAndUserIdModel;
import com.easyink.wecom.openapi.model.EncryptExternalUserIdAndUserIdModel;
import com.easyink.wecom.openapi.res.DecryptUserIdSelfBuildRes;
import com.easyink.wecom.openapi.service.LockSelfBuildApiService;
import com.easyink.wecom.openapi.service.ThirdService;
import com.easyink.wecom.service.WeChatContactMappingService;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeSensitiveActHitService;
import com.easyink.wecom.service.WeSensitiveService;
import com.easyink.wecom.service.idmapping.WeExternalUserIdMappingService;
import com.easyink.wecom.service.idmapping.WeUserIdMappingService;
import com.tencent.wework.FinanceUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 会话存档处理
 * @author tigger
 * 2024/11/5 15:06
 **/
@Slf4j
@Component
@AllArgsConstructor
public class SessionArchiveHandler {

    private final WeUpdateIDClient weUpdateIDClient;
    private final WeExternalUserIdMappingService weExternalUserIdMappingService;
    private final WeUserIdMappingService weUserIdMappingService;
    private final ElasticSearch elasticSearch;
    private final WeChatContactMappingService weChatContactMappingService;
    private final WeSensitiveService weSensitiveService;
    private final WeSensitiveActHitService weSensitiveActHitService;
    private final LockSelfBuildApiService lockSelfBuildApiService;
    private final LockSelfBuildConfigMapper lockSelfBuildConfigMapper;
    private final ThirdService thirdService;

    /**
     * 导入excel会话存档
     * @param is 文件流
     * @param openCorpId 加密的服务商企业id
     */
    public void importSessionArchive(InputStream is, String openCorpId, String serviceAgentId) {

        // 读取文件
        List<ImportSessionArchiveModel> dataList = readFile(is);
        if (dataList.isEmpty()) {
            log.info("[导入会话存档] 未读取到消息, openCorpId: {}", openCorpId);
            return;
        }

        // 请求企微处理映射数据
        // k: 原文 v: 密文
        DecryptExternalUserIdAndUserIdModel thirdServiceMappingModel = handleCiphertextMapping(dataList, openCorpId, serviceAgentId);


        // 组装会话数据并保存
        buildSessionArchiveAndSave(dataList, thirdServiceMappingModel, openCorpId);

    }


    /**
     * 处理会话实体构建和保存
     * @param dataList 导入的数据列表
     * @param thirdServiceMappingModel 第三方服务商映射map
     * @param corpId 企业id
     */
    private void buildSessionArchiveAndSave(List<ImportSessionArchiveModel> dataList, DecryptExternalUserIdAndUserIdModel thirdServiceMappingModel, String corpId) {
        AtomicInteger invalidCnt = new AtomicInteger();
        List<ChatInfoVO> chatMessageList = new ArrayList<>();


        BatchInsertUtil.doInsert(dataList, batchList -> {
            // 查询saas服务的映射关系batchList
            for (ImportSessionArchiveModel importSessionArchiveModel : batchList) {
                ChatInfoVO chatInfoVO = importSessionArchiveModel.toChatInfoVO(thirdServiceMappingModel);
                this.handleMsg(chatInfoVO, importSessionArchiveModel.getMsgType(), importSessionArchiveModel.getContent());
                if (chatInfoVO == null || chatInfoVO.invalid()) {
                    invalidCnt.getAndIncrement();
                    log.info("[导入会话存档] 非法的消息, data: {}", JSON.toJSONString(chatInfoVO));
                    continue;
                }
                // 处理数据
                if (MsgTypeEnum.FILE.getType().equals(chatInfoVO.getMsgtype())) {
                    // 设置一下文件名
                    if (StringUtils.isBlank(chatInfoVO.getFile().getFilename())) {
                        String defaultFileSuffix = "txt";
                        chatInfoVO.getFile().setFilename(String.valueOf(System.currentTimeMillis()).concat(".").concat(defaultFileSuffix));
                        chatInfoVO.getFile().setFileext(defaultFileSuffix);
                    }
                }
                FinanceUtils.getSwitchType(chatInfoVO, chatInfoVO.getMsgtype(), corpId);
                chatMessageList.add(chatInfoVO);
            }

        });

        log.info("[导入会话存档] 无效消息size: {}", invalidCnt);

        // 保存消息映射关系
        List<ChatInfoVO> elasticSearchEntities = weChatContactMappingService.saveWeChatContactMapping1(corpId, chatMessageList);
        //获取敏感行为命中信息
        weSensitiveActHitService.hitWeSensitiveAct1(corpId, chatMessageList);
        elasticSearch.insertBatch1(WeConstans.getChatDataIndex(corpId), elasticSearchEntities);
        weSensitiveService.hitSensitive1(corpId, elasticSearchEntities);

        log.info("[导入会话存档] 完成, 处理数据size: {}", chatMessageList.size());

    }

    /**
     * 处理消息内容
     * @param chatInfoVO 消息info
     * @param msgType 消息类型
     * @param content 导入的消息内容
     */
    private void handleMsg(ChatInfoVO chatInfoVO, Integer msgType, String content) {
        HaiderbaoMsgTypeEnum haiderbaoMsgTypeEnum = HaiderbaoMsgTypeEnum.getEnumByOriginType(msgType);
        if (haiderbaoMsgTypeEnum == null) {
            return;
        }
        switch (haiderbaoMsgTypeEnum) {
            case TEXT:
                TextVO textVO = new TextVO();
                textVO.setContent(content);
                chatInfoVO.setText(textVO);
                break;
            case FILE:
                chatInfoVO.setFile(JSONObject.parseObject(content, FileVO.class));
                break;
            case IMAGE:
                JSONObject imageJson = JSONObject.parseObject(content);
                ImageVO imageVO = JSONObject.parseObject(content, ImageVO.class);
                imageVO.setFilesize(imageJson.getString("imagesize"));
                chatInfoVO.setImage(imageVO);
                break;
            case VOICE:
                chatInfoVO.setVoice(JSONObject.parseObject(content, VoiceVO.class));
                break;
            case VIDEO:
                chatInfoVO.setVideo(JSONObject.parseObject(content, VideoVO.class));
                break;
            case OTHER:
                JSONObject jsonObject = JSONObject.parseObject(content);
                otherMsgHandle(jsonObject, chatInfoVO);
                break;
            default:
                log.info("[导入会话存档] 不处理的导入消息类型: {}", msgType);
                break;
        }
    }

    /**
     * 其他消息处理
     * @param jsonObject
     * @param chatInfoVO
     */
    private void otherMsgHandle(JSONObject jsonObject, ChatInfoVO chatInfoVO) {
        if (StringUtils.isNotBlank(jsonObject.getString("displayname"))) {
            // 小程序WeappVO
            chatInfoVO.setWeapp(jsonObject.toJavaObject(WeappVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.WEAPP.getType());
        } else if (StringUtils.isNotBlank(jsonObject.getString("corpname"))) {
            // 名片
            chatInfoVO.setCard(jsonObject.toJavaObject(CardVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.CARD.getType());
        } else if (CollectionUtils.isNotEmpty(jsonObject.getJSONArray("item"))) {
            chatInfoVO.setChatRecord(jsonObject.toJavaObject(ChatRecordVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.CHATRECORD.getType());
        } else if (StringUtils.isNotBlank(jsonObject.getString("feed_type"))) {
            chatInfoVO.setSphfeed(jsonObject.toJavaObject(SphFeedVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.SPHFEED.getType());
        }

    }

    /**
     * 处理密文映射
     *
     * @param dataList       导入的数据列表
     * @param corpId         企业id
     * @param serviceAgentId
     * @return 映射map
     */
    private DecryptExternalUserIdAndUserIdModel handleCiphertextMapping(List<ImportSessionArchiveModel> dataList, String corpId, String serviceAgentId) {
        // 获取需要映射的数据
        List<String> weworkExternalUserIdList = dataList.stream().map(ImportSessionArchiveModel::getUserId).collect(Collectors.toList());
        List<String> fromList = dataList.stream().map(ImportSessionArchiveModel::getFrom).collect(Collectors.toList());
        List<String> toList = dataList.stream().map(ImportSessionArchiveModel::getTo).collect(Collectors.toList());

        Set<String> needToMappingUserIdSet = new HashSet<>(weworkExternalUserIdList);
        Set<String> needToMappingExternalUserIdSet = new HashSet<>();
        // 移除员工数据
        fromList.removeIf(needToMappingUserIdSet::contains);
        toList.removeIf(needToMappingUserIdSet::contains);
        needToMappingExternalUserIdSet.addAll(fromList);
        needToMappingExternalUserIdSet.addAll(toList);

        if (needToMappingUserIdSet.isEmpty() || needToMappingExternalUserIdSet.isEmpty()) {
            log.info("导入会话存档] 映射数据异常, needToMappingUserIdSet.isEmpty(): {}, needToMappingExternalUserIdSet.isEmpty(): {}", needToMappingUserIdSet.isEmpty(), needToMappingExternalUserIdSet.isEmpty());
            return new DecryptExternalUserIdAndUserIdModel();
        }

        LockSelfBuildConfig lockSelfBuildConfig = lockSelfBuildConfigMapper.get(corpId);
        if (lockSelfBuildConfig == null) {
            throw new CustomException("未配置对应企业的自建应用信息");
        }
        WeCorpAccountService weCorpAccountService = SpringUtils.getBean(WeCorpAccountService.class);
        WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (corpAccount == null) {
            throw new CustomException("未配置对应企业信息");
        }

        log.info("导入会话存档] 需要解密的数据, userIdSize: {}, externalUserIdSize: {}", needToMappingUserIdSet.size(), needToMappingExternalUserIdSet.size());

        Map<String, String> userIdMapping = new HashMap<>();
        // 解密服务商的openUserId, 并保存到映射表
        List<String> userIds = new ArrayList<>(needToMappingUserIdSet);
        Integer userIdSuccessCnt = batchDecryptUserIdMapping(corpId, serviceAgentId, userIds, lockSelfBuildConfig.getDecryptUserIdUrl(),
                // 对解析到的员工映射关系,额外的处理
                mappingList -> {
                    weUserIdMappingService.batchInsertOrUpdateThirdService(mappingList);
                    for (WeUserIdMapping weUserIdMapping : mappingList) {
                        userIdMapping.put(weUserIdMapping.getOpenUserId(), weUserIdMapping.getUserId());
                    }
                });

        log.info("[导入会话存档] 保存userId映射数据大小: {}", userIdSuccessCnt);

        // 解密服务商的openExternalUserId, 并保存到映射表
        // k: 密文 v: 明文
        Map<String, String> externalUserIdMapping = new HashMap<>();
        List<String> openExternalUserIds = new ArrayList<>(needToMappingExternalUserIdSet);
        Integer externalUserIdSuccessCnt = batchDecryptExternalUserId(corpId, openExternalUserIds, serviceAgentId, lockSelfBuildConfig.getDecryptExternalUserIdUrl(),
                // 对解析到的外部联系人映射关系,额外的处理
                mappingList -> {
                    weExternalUserIdMappingService.batchInsertOrUpdateThirdService(mappingList);
                    for (WeExternalUseridMapping weExternalUseridMapping : mappingList) {
                        externalUserIdMapping.put(weExternalUseridMapping.getOpenExternalUserid(), weExternalUseridMapping.getExternalUserid());
                    }
                });
        log.info("导入会话存档] 保存externalUserId映射数据大小: {}", externalUserIdSuccessCnt);


        return new DecryptExternalUserIdAndUserIdModel(userIdMapping, externalUserIdMapping);
    }

    /**
     * 批量解密外部联系人id
     * @param corpId 企业id
     * @param openExternalUserIds 需要解密的openExternalUserId列表
     * @param agentId 待开发自建应用的agentId
     * @param selfBuildUrl 请求selfBuild模块的解密接口url
     * @param mappingDataInvoker 解密后回调函数处理
     * @return 解密成功数量
     */
    private Integer batchDecryptExternalUserId(String corpId, List<String> openExternalUserIds, String agentId, String selfBuildUrl, Consumer<List<WeExternalUseridMapping>> mappingDataInvoker) {
        AtomicInteger externalUserIdSuccessCnt = new AtomicInteger(0);
        BatchInsertUtil.doInsert(openExternalUserIds, (doList) -> {
            List<WeExternalUseridMapping> externalUseridMappingList = new ArrayList<>();
            for (String openExternalUserId : doList) {
                String decryptExternalUserId = lockSelfBuildApiService.decryptExternalUserId(openExternalUserId,
                        agentId, selfBuildUrl);
                if (StringUtils.isBlank(decryptExternalUserId)) {
                    continue;
                }
                externalUserIdSuccessCnt.incrementAndGet();
                externalUseridMappingList.add(new WeExternalUseridMapping(corpId, decryptExternalUserId, openExternalUserId));
            }
            if (mappingDataInvoker != null) {
                mappingDataInvoker.accept(externalUseridMappingList);
            }
        });
        return externalUserIdSuccessCnt.get();
    }

    /**
     * 批量解密员工id
     * @param corpId 企业id
     * @param serviceAgentId 第三方待开发自建应用的agentId
     * @param userIds 需要解密的员工id列表
     * @param decryptUserIdUrl 请求selfBuild模块的解密接口url
     * @param mappingDataInvoker 解密后回调函数处理
     * @return 解密成功数量
     */
    private Integer batchDecryptUserIdMapping(String corpId, String serviceAgentId, List<String> userIds, String decryptUserIdUrl, Consumer<List<WeUserIdMapping>> mappingDataInvoker) {
        AtomicInteger userIdSuccessCnt = new AtomicInteger(0);
        BatchInsertUtil.doInsert(userIds, (doList) -> {
            DecryptUserIdSelfBuildRes.GetUserIdResData decryptUserIdData = lockSelfBuildApiService.decryptUserId(doList,
                    serviceAgentId, decryptUserIdUrl);
            if (decryptUserIdData == null || CollectionUtils.isEmpty(decryptUserIdData.getUserIdList())) {
                return;
            }
            userIdSuccessCnt.addAndGet(decryptUserIdData.getUserIdList().size());
            List<DecryptUserIdSelfBuildRes.GetUserIdResData.OpenUserIdAndUserId> userIdList = decryptUserIdData.getUserIdList();
            // 根据响应 构建映射实体并入库
            List<WeUserIdMapping> mappingList = userIdList
                    .stream()
                    .map(it -> new WeUserIdMapping(corpId, it.getUserId(), it.getOpenUserId()))
                    .collect(Collectors.toList());
            if (mappingDataInvoker != null) {
                mappingDataInvoker.accept(mappingList);
            }

        }, 200);
        return userIdSuccessCnt.get();
    }

    /**
     * 读取文件
     * @param is 流
     * @return 导入的消息实体列表
     */
    private List<ImportSessionArchiveModel> readFile(InputStream is) {
        List<ImportSessionArchiveModel> dataList = new ArrayList<>();
        ExcelUtil<ImportSessionArchiveModel> excelUtil = new ExcelUtil<>(ImportSessionArchiveModel.class);
        try {
            dataList = excelUtil.importExcel(is);
            log.info("[导入会话存] 读取到的数据size: {}", dataList.size());
            return dataList;
        } catch (Exception e) {
            log.error("导入会话存档异常：ex={}", ExceptionUtils.getStackTrace(e));
            throw new CustomException("导入会话存档异常");
        }

    }

    /**
     * 同步saas服务的企业 员工和外部联系人 的映射关系
     * 注意: 该接口依赖数据库当前的数据进行保存, 如果没有同步到最近的员工和客户, 则不会保存其映射关系, 需要再系统上同步员工和同步客户后再进行该接口的调用
     * 是否可重复执行? 可
     * @param corpId 企业id
     */
    public void syncMapping(String corpId) {
        LockSelfBuildConfig lockSelfBuildConfig = lockSelfBuildConfigMapper.get(corpId);
        if (lockSelfBuildConfig == null) {
            throw new CustomException("未配置对应企业的自建应用信息");
        }
        WeCorpAccountService weCorpAccountService = SpringUtils.getBean(WeCorpAccountService.class);
        WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (corpAccount == null) {
            throw new CustomException("未配置对应企业信息");
        }


        // 1.解密 userId并保存映射关系
        List<String> userIdList = thirdService.listUserIdByCorpId(corpId);
        Integer userIdSuccessCnt = batchDecryptUserIdMapping(corpId, corpAccount.getAgentId(), userIdList, lockSelfBuildConfig.getDecryptUserIdUrl(),
                // 对解析到的员工映射关系,额外的处理
                weUserIdMappingService::batchInsertOrUpdate);
        log.info("[同步映射关系] 保存userId映射数据大小: {}", userIdSuccessCnt);


        // 2.解密 externalUserId并保存映射关系
        List<String> externalUserIdList = thirdService.listExternalUserIdByCorpId(corpId);

        Integer externalUserIdSuccessCnt = batchDecryptExternalUserId(corpId, externalUserIdList, corpAccount.getAgentId(), lockSelfBuildConfig.getDecryptExternalUserIdUrl(),
                // 对解析到的外部联系人映射关系,额外的处理
                weExternalUserIdMappingService::batchInsertOrUpdate);
        log.info("同步映射关系] 保存externalUserId映射数据大小: {}", externalUserIdSuccessCnt);

        log.info("同步映射关系] 同步完成, userIdSize: {}, externalUserIdSize: {}", userIdSuccessCnt, externalUserIdSuccessCnt);

    }
}
