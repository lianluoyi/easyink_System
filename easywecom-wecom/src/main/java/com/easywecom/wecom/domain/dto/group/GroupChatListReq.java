package com.easywecom.wecom.domain.dto.group;

import com.easywecom.common.utils.spring.SpringUtils;
import com.easywecom.wecom.client.WeCustomerGroupClient;
import com.easywecom.wecom.domain.dto.WePageBaseReq;
import com.easywecom.wecom.domain.dto.WePageBaseResp;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: 获取客户群列表请求实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 19:15
 */
@Data
@Builder
public class GroupChatListReq extends WePageBaseReq<GroupChatListResp.GroupChat> {
    /**
     * 客户群跟进状态过滤。
     * 0 - 所有列表(即不过滤)
     * 1 - 离职待继承
     * 2 - 离职继承中
     * 3 - 离职继承完成
     * <p>
     * 默认为0
     */
    private Integer status_filter;
    /**
     * 群主过滤。
     * 如果不填，表示获取应用可见范围内全部群主的数据（但是不建议这么用，如果可见范围人数超过1000人，为了防止数据包过大，会报错 81017）
     */
    private OwnerFilter owner_filter;

    private final Integer limit  = 100;

    @Data
    public static class OwnerFilter {
        /**
         * 用户ID列表。最多100个
         */
        private List<String> userid_list;
    }

    @Override
    public WePageBaseResp<GroupChatListResp.GroupChat> execute(String corpId) {
        WeCustomerGroupClient client = SpringUtils.getBean(WeCustomerGroupClient.class);
        return client.groupChatList(this,corpId);
    }

    /**
     * 设置群主筛选条件
     *
     * @param userId 群主id
     */
    public void setOwnerFilter (String userId) {
        OwnerFilter filter  = new OwnerFilter();
        filter.userid_list = new ArrayList<>();
        filter.userid_list.add(userId);
        this.owner_filter = filter;
    }



}
