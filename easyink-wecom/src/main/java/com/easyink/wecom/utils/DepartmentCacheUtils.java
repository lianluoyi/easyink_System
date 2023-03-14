package com.easyink.wecom.utils;


import cn.hutool.core.collection.CollUtil;
import com.easyink.common.core.domain.wecom.WeDepartment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 缓存可见范围内的部门工具类
 *
 * @author wx
 * 2023/3/3 10:48
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentCacheUtils {

    /**
     * 枚举方式实现单例
     */
    private enum DepartmentCacheEnum {
        /**
         * 单例
         */
        INSTANCE;

        /**
         * key: 企业， value:存储可见范围内的部门id
         */
        @Getter
        private Map<String, Set<Long>> corpIdDepartmentIdsMap;

        /**
         * 定义一个线程安全的map
         */
        DepartmentCacheEnum() {
            corpIdDepartmentIdsMap = new ConcurrentHashMap<>();
        }
    }

    /**
     * 提供单例出口
     *
     * @return  instance
     */
    protected static DepartmentCacheEnum getInstance() {
        //获取单例对象，返回
        return DepartmentCacheEnum.INSTANCE;
    }

    /**
     * 清空容器并设置
     *
     * @param corpId        企业id
     * @param departments   部门list
     */
    public static void clearAndSet(String corpId, List<WeDepartment> departments) {
        if (StringUtils.isBlank(corpId) || CollUtil.isEmpty(departments)) {
            return;
        }
        Set<Long> departmentIds = departments.stream().map(WeDepartment::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (getInstance().getCorpIdDepartmentIdsMap().containsKey(corpId)) {
            getInstance().getCorpIdDepartmentIdsMap().get(corpId).clear();
            getInstance().getCorpIdDepartmentIdsMap().get(corpId).addAll(departmentIds);
        } else {
            getInstance().getCorpIdDepartmentIdsMap().put(corpId, departmentIds);
        }
    }

    /**
     * 判断departmentId是否为可见范围内的部门
     *
     * @param corpId        企业id
     * @param departmentId  部门id
     * @return
     */
    public static boolean isMember(String corpId, Long departmentId) {
        if (StringUtils.isBlank(corpId) || departmentId == null) {
            return false;
        }
        Map<String, Set<Long>> corpIdDepartmentIdsMap = getInstance().getCorpIdDepartmentIdsMap();
        if (corpIdDepartmentIdsMap.containsKey(corpId)) {
            return corpIdDepartmentIdsMap.get(corpId).contains(departmentId);
        }
        return false;
    }

    /**
     * 判断departmentId是否为可见范围内的部门
     *
     * @param corpId        企业id
     * @param departmentId  部门id
     * @return
     */
    public static boolean isMember(String corpId, String[] departmentId) {
        if (departmentId == null) {
            return false;
        }
        int firstIndex = 0;
        Long deptId = Long.parseLong(departmentId[firstIndex]);
        return isMember(corpId, deptId);
    }


}
