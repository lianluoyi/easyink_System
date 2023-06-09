package com.easyink.common.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 分割集合工具
 *
 * @author : silver_chariot
 * @date : 2023/6/8 9:32
 **/
public class ListShardUtil {
    /**
     * 将集合拆分固定的分片
     *
     * @param list    结合
     * @param maxSize 分片数
     * @param <T>     集合元素泛型
     * @return 分片后的集合
     */
    public static <T> List<List<T>> split2MaxSize(List<T> list, Integer maxSize) {
        List<List<T>> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        if (maxSize == null || maxSize <= 0) {
            resultList.add(list);
            return resultList;
        }
        // 决定分片数
        int splitSize = list.size() < maxSize ? list.size() : maxSize;
        for (int i = 0; i < splitSize; i++) {
            resultList.add(new ArrayList<>());
        }
        for (int i = 0; i < list.size(); i++) {
            resultList.get(i % splitSize).add(list.get(i));
        }
        return resultList;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            list.add(i);
        }
        List<List<Integer>> res = split2MaxSize(list, 5);
        System.out.println(res);
    }


}
