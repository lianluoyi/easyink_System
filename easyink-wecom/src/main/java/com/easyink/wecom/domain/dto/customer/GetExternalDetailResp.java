package com.easyink.wecom.domain.dto.customer;

import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeFlowerCustomerTagRel;
import com.easyink.wecom.domain.dto.WeResultDTO;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名: 企微API 获取客户详情/externalcontact/get响应实体
 *
 * @author : silver_chariot
 * @date : 2021/11/5 16:11
 */
@Data
public class GetExternalDetailResp extends WeResultDTO {
    //** 企微 官方API返回的数据
    /**
     * 外部联系人详情
     */
    private ExternalContact externalContact;
    /**
     * 跟进人详情列表
     */
    private List<FollowInfo> follow_user;
    //** 处理(调用HandleData)后的数据
    /**
     * 处理后的客户详情,由请求后调用handleData后获得
     */
    private WeCustomer remoteCustomer;
    /**
     * 处理后的跟进人信息,由请求后调用handleData后获得
     */
    private WeFlowerCustomerRel remoteRel;
    /**
     * 跟进人给该客户打的标签 ,由请求后调用handleData后获得
     */
    private List<String> tagIds;

    /**
     * 对返回的数据进行数据处理
     *
     * @param corpId 公司ID
     * @param userId 跟进人userId
     */
    public void handleData(String corpId, String userId) {
        // 由远端数据构建客户实体
        remoteCustomer = new WeCustomer(externalContact, corpId);
        // 在远端返回的跟进人数据中根据userId筛选出指定跟进人
        FollowInfo followInfo = getFollowUserById(userId);
        if (followInfo != null) {
            // 由远端跟进人和客户数据 构建客户关系实体
            remoteRel = new WeFlowerCustomerRel(externalContact, followInfo, corpId);
            // 保存该跟进人打的标签信息
            if (CollectionUtils.isNotEmpty(followInfo.getTags())) {
                tagIds = followInfo.getTags().stream().map(FollowInfo.Tags::getTag_id).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            }
        }
    }

    /**
     * 根据userId获取返回跟进人列表里的筛选指定的跟进人
     *
     * @param userId 成员ID
     * @return {@link FollowInfo}
     */
    public FollowInfo getFollowUserById(String userId) {
        if (StringUtils.isBlank(userId) || CollectionUtils.isEmpty(follow_user)) {
            return null;
        }
        return follow_user.stream().filter(follower -> userId.equals(follower.getUserId())).findFirst().orElse(null);
    }

    /**
     * 根据返回数据构造 客户-标签实体集合
     *
     * @param rel {@link WeFlowerCustomerRel}
     * @return {@link List<WeFlowerCustomerTagRel>}
     */
    public List<WeFlowerCustomerTagRel> getTagRelList(WeFlowerCustomerRel rel) {
        if (rel == null || rel.getId() == null || CollectionUtils.isEmpty(tagIds)) {
            return Collections.emptyList();
        }
        List<WeFlowerCustomerTagRel> tagRelList = new ArrayList<>();
        for (String tagId : tagIds) {
            tagRelList.add(
                    WeFlowerCustomerTagRel.builder()
                            .flowerCustomerRelId(rel.getId())
                            .tagId(tagId)
                            .externalUserid(externalContact.getExternalUserid())
                            .build()
            );
        }
        return tagRelList;
    }

    /**
     * 判断API返回的数据结果是否为空
     *
     * @return true or false
     */
    public boolean isEmptyResult() {
        return externalContact == null || StringUtils.isBlank(externalContact.getExternalUserid());
    }

}
