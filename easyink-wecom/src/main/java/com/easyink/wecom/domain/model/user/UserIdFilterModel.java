package com.easyink.wecom.domain.model.user;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tigger
 * 2024/2/7 15:00
 **/
@Data
public class UserIdFilterModel {


    /**
     * 是否过滤判断
     */
    private boolean filterFlag;

    /**
     * 客户id列表
     */
    private List<String> idList = new ArrayList<>();


    /**
     * 添加客户id, and条件, 所以取交集就好
     * @param ids
     */
    public void addIdAndConditionFilter(List<String> ids) {
        this.filterFlag = true;
        if (CollectionUtils.isEmpty(ids)) {
            // 空集合取交集, 返回空
            setIdList(new ArrayList<>());
            return;
        }
        if (CollectionUtils.isEmpty(getIdList())) {
            setIdList(ids);
        } else {
            List<String> origins = getIdList();
            List<String> temp = new ArrayList<>();
            // 取交集
            for (String current : ids) {
                if (origins.contains(current)) {
                    temp.add(current);
                }
            }
            setIdList(temp);
        }
    }

    public void clearAll() {
        this.filterFlag = true;
        setIdList(new ArrayList<>());
    }

    /**
     * 如果是超级管理员和网点管理员,不需要权限校验,用filterFlag = flse来表示不需要根据filterCustomerIdList来作为过滤条件判断
     * @return
     */
    public UserIdFilterModel notFilter() {
        this.filterFlag = false;
        return this;
    }

    /**
     * 如果是客服主管和客服,需要权限校验,用filterFlag = true来表示需要根据filterCustomerIdList来作为过滤条件判断
     * @return
     */
    public UserIdFilterModel filter(List<String> ids) {
        this.filterFlag = true;
        this.idList = ids;
        return this;
    }


}
