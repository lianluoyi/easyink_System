package com.easyink.wecom.domain.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tigger
 * 2023/12/29 9:52
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRefIdFilterModel {
    /**
     * 是否过滤判断
     */
    private boolean filterFlag;

    /**
     * 客户id列表
     */
    private Set<Long> filterIdList = new HashSet<>();

    /**
     * 数据添加, 取交集
     * @param dataIdList
     */
    public void addAndConditionFilter(Set<Long> dataIdList) {
        this.filterFlag = true;
        if (CollectionUtils.isEmpty(dataIdList)) {
            // 空集合取交集, 返回空
            setFilterIdList(new HashSet<>());
            return;
        }
        if (CollectionUtils.isEmpty(getFilterIdList())) {
            setFilterIdList(dataIdList);
        } else {
            Set<Long> origins = getFilterIdList();
            Set<Long> temp = new HashSet<>();
            // 取交集
            for (Long current : dataIdList) {
                if (origins.contains(current)) {
                    temp.add(current);
                }
            }
            setFilterIdList(temp);
        }
    }

    /**
     *  数据添加, 取并集
     * @param dataIdList
     */
    public void addOrConditionFilter(Set<Long> dataIdList) {
        this.filterFlag = true;
        if (CollectionUtils.isEmpty(dataIdList)) {
            return;
        }
        getFilterIdList().addAll(dataIdList);
    }
}
