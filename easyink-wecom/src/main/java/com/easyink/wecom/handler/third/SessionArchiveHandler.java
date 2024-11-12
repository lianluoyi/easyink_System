package com.easyink.wecom.handler.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.conversation.msgtype.*;
import com.easyink.common.core.elasticsearch.ElasticSearch;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.client.WeUpdateIDClient;
import com.easyink.wecom.domain.dto.CorpIdToOpenCorpIdResp;
import com.easyink.wecom.domain.entity.WeExternalUseridMapping;
import com.easyink.wecom.domain.enums.third.HaiderbaoMsgTypeEnum;
import com.easyink.wecom.domain.model.third.ImportSessionArchiveModel;
import com.easyink.wecom.mapper.WeExternalUseridMappingMapper;
import com.easyink.wecom.service.WeChatContactMappingService;
import com.easyink.wecom.service.WeSensitiveActHitService;
import com.easyink.wecom.service.WeSensitiveService;
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
    private final WeExternalUseridMappingMapper weExternalUseridMappingMapper;
    private final ElasticSearch elasticSearch;
    private final WeChatContactMappingService weChatContactMappingService;
    private final WeSensitiveService weSensitiveService;
    private final WeSensitiveActHitService weSensitiveActHitService;

    public void importSessionArchive(InputStream is, String corpId) {

        // 读取文件
        List<ImportSessionArchiveModel> dataList = readFile(is);
        if (dataList.isEmpty()) {
            log.info("[导入会话存档] 未读取到消息, corpId: {}", corpId);
            return;
        }

        // 请求企微处理映射数据
        // k: 原文 v: 密文
        Map<String, String> dataMapping = handleCiphertextMapping(dataList, corpId);

        // 组装会话数据并保存
        buildSessionArchiveAndSave(dataList, dataMapping, corpId);

    }


    /**
     * 处理会话实体构建和保存
     * @param dataList 导入的数据列表
     * @param dataMapping 字段映射map
     * @param corpId 企业id
     */
    private void buildSessionArchiveAndSave(List<ImportSessionArchiveModel> dataList, Map<String, String> dataMapping, String corpId) {
        int invalidCnt = 0;
        List<ChatInfoVO> chatMessageList = new ArrayList<>();
        for (ImportSessionArchiveModel importSessionArchiveModel : dataList) {
            ChatInfoVO chatInfoVO = importSessionArchiveModel.toChatInfoVO(dataMapping);
            this.handleMsg(chatInfoVO, importSessionArchiveModel.getMsgType(), importSessionArchiveModel.getContent());
            if (chatInfoVO == null || chatInfoVO.invalid()) {
                invalidCnt++;
                log.info("[导入会话存档] 非法的消息, data: {}", JSON.toJSONString(chatInfoVO));
                continue;
            }
            // 处理数据
            if(MsgTypeEnum.FILE.getType().equals(chatInfoVO.getMsgtype())){
                // 设置一下文件名
                if(StringUtils.isBlank(chatInfoVO.getFile().getFilename())){
                    String defaultFileSuffix = "txt";
                    chatInfoVO.getFile().setFilename(String.valueOf(System.currentTimeMillis()).concat(".").concat(defaultFileSuffix));
                    chatInfoVO.getFile().setFileext(defaultFileSuffix);
                }
            }
            FinanceUtils.getSwitchType(chatInfoVO, chatInfoVO.getMsgtype(), corpId);
            chatMessageList.add(chatInfoVO);
        }
        log.info("[导入会话存档] 无效消息size: {}", invalidCnt);

        // 保存消息映射关系
        List<ChatInfoVO> elasticSearchEntities = weChatContactMappingService.saveWeChatContactMapping1(corpId, chatMessageList);
        //获取敏感行为命中信息
        weSensitiveActHitService.hitWeSensitiveAct1(corpId, chatMessageList);
        elasticSearch.insertBatch1(WeConstans.getChatDataIndex(corpId), elasticSearchEntities);
        weSensitiveService.hitSensitive1(corpId, elasticSearchEntities);

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
        if(StringUtils.isNotBlank(jsonObject.getString("displayname"))){
            // 小程序WeappVO
            chatInfoVO.setWeapp(jsonObject.toJavaObject(WeappVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.WEAPP.getType());
        }else if(StringUtils.isNotBlank(jsonObject.getString("corpname"))){
            // 名片
            chatInfoVO.setCard(jsonObject.toJavaObject(CardVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.CARD.getType());
        }else if(CollectionUtils.isNotEmpty(jsonObject.getJSONArray("item"))){
            chatInfoVO.setChatRecord(jsonObject.toJavaObject(ChatRecordVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.CHATRECORD.getType());
        }else if(StringUtils.isNotBlank(jsonObject.getString("feed_type"))){
            chatInfoVO.setSphfeed(jsonObject.toJavaObject(SphFeedVO.class));
            chatInfoVO.setMsgtype(MsgTypeEnum.SPHFEED.getType());
        }

    }

    /**
     * 处理密文映射
     * @param dataList 导入的数据列表
     * @param corpId 企业id
     * @return 映射map
     */
    private Map<String, String> handleCiphertextMapping(List<ImportSessionArchiveModel> dataList, String corpId) {
        // 获取需要映射的数据
        List<String> weworkExternalUserIdList = dataList.stream().map(ImportSessionArchiveModel::getUserId).collect(Collectors.toList());
        List<String> fromList = dataList.stream().map(ImportSessionArchiveModel::getFrom).collect(Collectors.toList());
        List<String> toList = dataList.stream().map(ImportSessionArchiveModel::getTo).collect(Collectors.toList());

        Set<String> needToMappingSet = new HashSet<>();
        needToMappingSet.addAll(weworkExternalUserIdList);
        needToMappingSet.addAll(fromList);
        needToMappingSet.addAll(toList);

        if (needToMappingSet.isEmpty()) {
            return new HashMap<>();
        }
        log.info("导入会话存档] 需要映射的数据size: {}", needToMappingSet.size());

        Map<String, String> mapping = new HashMap<>();
        List<String> externalUserIds = new ArrayList<>(needToMappingSet);
        BatchInsertUtil.doInsert(externalUserIds, (doList) -> {
            final CorpIdToOpenCorpIdResp newExternalUserRes = weUpdateIDClient.getNewExternalUserid(corpId, externalUserIds);
            Map<String, String> openExternalUserIdMap = newExternalUserRes.getItems().stream().collect(Collectors.toMap(CorpIdToOpenCorpIdResp.ExternalUserMapping::getExternal_userid, CorpIdToOpenCorpIdResp.ExternalUserMapping::getNew_external_userid));
            mapping.putAll(openExternalUserIdMap);
        }, 200);
        log.info("导入会话存档] 请求企微获取到的映射数据size: {}", mapping.size());
        List<WeExternalUseridMapping> mappingList = new ArrayList<>(mapping.size());
        mapping.forEach((k, v) -> {
            mappingList.add(new WeExternalUseridMapping(corpId, k, v));
        });

        // 保存到数据库
        AtomicInteger insertCnt = new AtomicInteger(0);
        BatchInsertUtil.doInsert(mappingList, doList -> {
            int res = weExternalUseridMappingMapper.batchInsertOrUpdate(doList);
            insertCnt.addAndGet(res);
        });
        log.info("[导入会话存档] 保存成功映射数据size: {}", insertCnt.get());
        return mapping;
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
}
