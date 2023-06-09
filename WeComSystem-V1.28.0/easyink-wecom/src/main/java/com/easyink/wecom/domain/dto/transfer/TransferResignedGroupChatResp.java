package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.constant.GroupConstants;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.entity.transfer.WeResignedGroupTransferRecord;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名: 分配离职成员的客户群响应实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 19:45
 */
@Data
public class TransferResignedGroupChatResp extends WeResultDTO{
    /**
     * 没继承成功的群详情
     */
    private List<FailResult> failed_chat_list;


    @Data
    public class FailResult {
        /**
         * 没能成功继承的群ID
         */
        private String chat_id;
        /**
         * 没能成功继承的群，错误码
         */
        private Integer errcode;
        /**
         * 没能成功继承的群，错误描述
         */
        private String errmsg;
    }

    /**
     * 返回空的结果
     *
     * @return {@link TransferResignedGroupChatResp }
     */
    public static TransferResignedGroupChatResp getEmptyResult() {
        TransferResignedGroupChatResp resp = new TransferResignedGroupChatResp();
        resp.setFailed_chat_list(Collections.emptyList());
        return resp;
    }

    /**
     * 根据返回的信息构建分配客户群记录列表
     *
     * @param recordId   总记录 id
     * @param chatIdList 群聊id 集合
     * @return {@link List<WeResignedGroupTransferRecord>}
     */
    public List<WeResignedGroupTransferRecord> getRecordList(Long recordId, List<String> chatIdList) {
        if (recordId == null || CollectionUtils.isEmpty(chatIdList)) {
            return Collections.emptyList();
        }
        List<WeResignedGroupTransferRecord> recordList = new ArrayList<>();
        Date now = new Date();

        Map<String, FailResult> map = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(this.failed_chat_list)) {
            map = failed_chat_list.stream().collect(Collectors.toMap(FailResult::getChat_id, res -> res));
        }
        // 构建记录实体并存入集合中
        for (String chatId : chatIdList) {
            WeResignedGroupTransferRecord record = WeResignedGroupTransferRecord.builder()
                    .chatId(chatId)
                    .recordId(recordId)
                    .takeoverTime(now)
                    .status(Boolean.TRUE)
                    .remark(StringUtils.EMPTY)
                    .build();
            if (map.containsKey(chatId)) {
                FailResult failResult = map.get(chatId);
                record.setStatus(Boolean.FALSE);
                record.setRemark(WeExceptionTip.getTipMsg(failResult.getErrcode(), failResult.getErrmsg()));
            }
            recordList.add(record);
        }
        return recordList;
    }

    /**
     * 获取成功接替的客户群 集合
     *
     * @param chatIdList     本次分配的所有客户群id集合
     * @param takeoverUserId 接替人userId
     * @return 成功分配的客户群实体集合
     */
    public List<WeGroup> getSuccessTransferGroup(List<String> chatIdList, String takeoverUserId) {
        if (CollectionUtils.isEmpty(chatIdList)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isNotEmpty(getFailed_chat_list())) {
            List<String> failIdList = getFailed_chat_list().stream().map(FailResult::getChat_id).collect(Collectors.toList());
            // 过滤掉所有接替失败的群聊ID
            chatIdList = chatIdList.stream().filter(a -> !failIdList.contains(a)).collect(Collectors.toList());
        }
        List<WeGroup> list = new ArrayList<>();
        for (String chatId : chatIdList) {
            // 构建接替后的群聊实体并存入集合中
            list.add(
                    WeGroup.builder()
                            .chatId(chatId)
                            .owner(takeoverUserId)
                            .status(GroupConstants.NARMAL)
                            .build()
            );
        }
        return list;
    }
}
