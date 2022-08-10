package com.easyink.wecom.domain.dto.customer.req;

import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.domain.req.WePageBaseReq;
import com.easyink.wecom.domain.resp.WePageBaseResp;
import com.easyink.wecom.domain.dto.customer.resp.GetByUserResp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: 企微get_by_user请求实体
 *
 * @author : silver_chariot
 * @date : 2021/11/1 11:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class GetByUserReq extends WePageBaseReq<GetByUserResp.ExternalContactDetail> {

    /**
     * 企业成员的userid列表，字符串类型，最多支持100个
     */
    private List<String> userid_list;
    /**
     * 返回的最大记录数，整型，最大值100，默认值50，超过最大值时取最大值
     */
    private final int limit = 100;


    public GetByUserReq(String userId) {
        List<String> list = new ArrayList<>();
        list.add(userId);
        this.userid_list = list;
    }

    @Override
    public WePageBaseResp<GetByUserResp.ExternalContactDetail> execute(String corpId) {
        WeCustomerClient weCustomerClient = SpringUtils.getBean(WeCustomerClient.class);
        return weCustomerClient.getByUser(this, corpId);
    }

}
