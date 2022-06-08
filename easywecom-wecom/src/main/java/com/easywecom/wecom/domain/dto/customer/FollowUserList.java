package com.easywecom.wecom.domain.dto.customer;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author admin
 * @description: 获取配置了客户联系功能的成员列表
 * @create: 2020-10-19 22:08
 **/
@Data
public class FollowUserList extends WeResultDTO {

    private String[] follow_user;

    /**
     * 获取 配置了“联系我”的 成员列表
     *
     * @return 成员userId数组, 如果没有会返回空数组
     */
    public String[] getFollow_user() {
        if (ArrayUtils.isEmpty(follow_user)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return follow_user;
    }

    /**
     * 获取 配置了“联系我”的 成员列表
     *
     * @return 成员userId列表, 如果没有会返回空集合
     */
    public List<String> getFollowerUserIdList() {
        if (ArrayUtils.isEmpty(follow_user)) {
            return Collections.emptyList();
        }
        return Arrays.asList(follow_user);
    }
}
