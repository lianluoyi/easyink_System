package com.easyink.wecom.domain.query.customer;

import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.model.customer.CustomerIdFilterModel;
import com.easyink.wecom.domain.model.customer.CustomerRefIdFilterModel;
import com.easyink.wecom.domain.model.customer.UserIdAndExternalUserIdModel;
import com.easyink.wecom.domain.model.user.UserIdFilterModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 查询上下文
 * @author tigger
 * 2023/12/28 16:24
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerQueryContext extends WeCustomer {

    private boolean hasDataScope = true;

    private boolean leftJoinCustomerTable = false;

    public void setLeftJoinCustomerTable(boolean leftJoinCustomerTable) {
        this.leftJoinCustomerTable = leftJoinCustomerTable;
    }

    /**
     * 企业id
     */
    private String corpId;
    /**
     * 客户状态
     */
    private String status;
    /**
     * 主键id列表
     */
    private CustomerRefIdFilterModel refIdFilter = new CustomerRefIdFilterModel();

    /**
     * 客户id filter
     */
    private CustomerIdFilterModel customerIdFilter = new CustomerIdFilterModel();

    /**
     * 员工id filter
     */
    private UserIdFilterModel userIdFilter = new UserIdFilterModel();

    /**
     * 员工和客户id列表
     */
    private List<UserIdAndExternalUserIdModel> userExternalList;
    private List<WeFlowerCustomerRel> weFlowerCustomerRels;


    /**
     * 没有查询数据
     * @return
     */
    public CustomerQueryContext notDataSelect() {
        this.hasDataScope = false;
        return this;
    }
}
