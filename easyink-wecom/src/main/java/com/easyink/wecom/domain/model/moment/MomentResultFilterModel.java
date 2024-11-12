package com.easyink.wecom.domain.model.moment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 朋友圈结果过滤model
 * @author tigger
 * 2023/12/26 15:32
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentResultFilterModel {
    /**
     * 是否过滤判断
     */
    private boolean filterFlag;

    /**
     * 客户id列表
     */
    private List<Long> filterResultIdList = new ArrayList<>();

    /**
     * 朋友圈结果id过滤, 取交集
     * @param filterIds
     */
    public void addDetailIdAndConditionFilter(List<Long> filterIds) {
        this.filterFlag = true;
        if (CollectionUtils.isEmpty(filterIds)) {
            // 空集合取交集, 返回空
            setFilterResultIdList(new ArrayList<>());
            return;
        }
        if (CollectionUtils.isEmpty(getFilterResultIdList())) {
            setFilterResultIdList(filterIds);
        } else {
            List<Long> origins = getFilterResultIdList();
            List<Long> temp = new ArrayList<>();
            // 取交集
            for (Long current : filterIds) {
                if (origins.contains(current)) {
                    temp.add(current);
                }
            }
            setFilterResultIdList(temp);
        }
    }
}
