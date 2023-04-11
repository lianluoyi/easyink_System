package com.easyink.wecom.domain.dto.group;

import com.easyink.wecom.domain.resp.WePageBaseResp;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类名: 获取客户群列表响应实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 19:16
 */
@Data
public class GroupChatListResp extends WePageBaseResp<GroupChatListResp.GroupChat> {
    /**
     * 客户群列表
     */
    private List<GroupChat> group_chat_list;

    @Data
    public class GroupChat {
        /**
         * 客户群ID
         */
        private String chat_id;
        /**
         * 客户群跟进状态。
         * 0 - 跟进人正常
         * 1 - 跟进人离职
         * 2 - 离职继承中
         * 3 - 离职继承完成
         */
        private Integer status;
    }

    @Override
    public List<GroupChat> getPageList() {
        if (CollectionUtils.isEmpty(group_chat_list)) {
            return Collections.emptyList();
        }
        return group_chat_list;
    }

    @Override
    public void handleData(String corpId, Map<String, String> userIdInDbMap) {

    }

    /**
     * 获取所有群聊ID集合
     *
     * @return 群聊id集合
     */
    public List<String> getChatIdList() {
        if (CollectionUtils.isEmpty(getTotalList())) {
            return Collections.emptyList();
        }
        return getTotalList().stream().map(GroupChatListResp.GroupChat::getChat_id).collect(Collectors.toList());
    }

}
