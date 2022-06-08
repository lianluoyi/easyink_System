package com.easywecom.wecom.domain.dto.customer.resp;

import com.easywecom.common.enums.CustomerStatusEnum;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeFlowerCustomerRel;
import com.easywecom.wecom.domain.WeFlowerCustomerTagRel;
import com.easywecom.wecom.domain.dto.WePageBaseResp;
import com.easywecom.wecom.domain.dto.customer.ExternalContact;
import com.easywecom.wecom.domain.dto.customer.FollowInfo;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名: 企微get_by_user响应实体
 *
 * @author : silver_chariot
 * @date : 2021/11/1 10:54
 */
@Data
public class GetByUserResp extends WePageBaseResp<GetByUserResp.ExternalContactDetail> {


    /**
     * 客户的基本信息集合,由企微API返回
     */
    private List<ExternalContactDetail> external_contact_list;

    /**
     * 处理后的客户集合,由访问API,并调用handleData数据处理后获得
     */
    private List<WeCustomer> customerList = new ArrayList<>();
    /**
     * 处理后的客户-成员关系集合,由访问API,并调用handleData后数据处理后获得
     */
    private List<WeFlowerCustomerRel> relList = new ArrayList<>();
    /**
     * 处理后的客户-标签关系映射 ,由访问API,并调用handleData后数据处理后获得
     */
    private Map<WeFlowerCustomerRel, String[]> customerTagMap = new HashMap<>();


    /**
     * 企微API 客户基本信息实体
     */
    @Data
    public class ExternalContactDetail {
        /**
         * 外部联系人
         */
        public ExternalContact external_contact;
        /**
         * 跟进人信息
         */
        public FollowInfo follow_info;
    }

    @Override
    public List<ExternalContactDetail> getPageList() {
        if (CollectionUtils.isEmpty(external_contact_list)) {
            return Collections.emptyList();
        }
        return external_contact_list;
    }

    /**
     * 判断是否有返回客户信息
     *
     * @return true or false
     */
    public boolean isEmptyResult() {
        return CollectionUtils.isEmpty(getTotalList());
    }

    @Override
    public void handleData(String corpId) {
        if (CollectionUtils.isEmpty(getTotalList())) {
            return;
        }
        // 数据格式转换: 企微API返回数据 转换=> 客户集合,客户成员关系实体
        for (ExternalContactDetail detail : getTotalList()) {
            customerList.add(new WeCustomer(detail.getExternal_contact(), corpId));
            WeFlowerCustomerRel rel = new WeFlowerCustomerRel(detail, corpId);
            relList.add(rel);
            //如果存在标签则存入客户-标签映射
            if (ArrayUtils.isNotEmpty(detail.getFollow_info().getTag_id())) {
                customerTagMap.put(rel, detail.getFollow_info().getTag_id());
            }

        }
    }

    /**
     * 根据本次获取的数据,构建客户-标签实体关系集合
     *
     * @param localRelList 本地数据库中的成员-客户关系实体集合
     */
    public List<WeFlowerCustomerTagRel> getCustomerTagRelList(List<WeFlowerCustomerRel> localRelList) {
        // 成员客户-标签列表
        List<WeFlowerCustomerTagRel> tagRelList = new ArrayList<>();
        for (WeFlowerCustomerRel localRel : localRelList) {
            // 遍历本地的成员客户关系,判断是否存在于拉取结果处理后的客户-标签映射中
            if (customerTagMap.containsKey(localRel)) {
                //存在则获取其标签
                String[] tagIds = customerTagMap.get(localRel);
                // 从本地标签关系实体中获取ID
                Long relId = localRel.getId();
                if (ArrayUtils.isEmpty(tagIds)) {
                    continue;
                }
                // 遍历标签,构建 客户-标签关系实体,并加入集合中,待入库
                for (String tagId : tagIds) {
                    tagRelList.add(new WeFlowerCustomerTagRel(relId, tagId, localRel.getExternalUserid()));
                }

            }
        }
        return tagRelList;

    }

    /**
     * 重新激活原来被删除或者流失的客户
     *
     * @param localRelList {@link List<WeFlowerCustomerRel>  }
     */
    public void activateDelCustomer(List<WeFlowerCustomerRel> localRelList) {
        if (CollectionUtils.isEmpty(localRelList) || CollectionUtils.isEmpty(relList)) {
            return;
        }
        Map<WeFlowerCustomerRel, String> statusMap = localRelList.stream().collect(Collectors.toMap(rel -> rel, WeFlowerCustomerRel::getStatus));
        for (WeFlowerCustomerRel rel : relList) {
            if (statusMap.containsKey(rel)) {
                String status = statusMap.get(rel);
                if (status == null) {
                    continue;
                }
                // 如果之前就存在该客户关系 ,则把流失/删除状态重置为正常
                rel.setStatus(CustomerStatusEnum.isDel(Integer.valueOf(status)) ? CustomerStatusEnum.NORMAL.getCode().toString() : status);
            }
        }
    }


}
