package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeGroupTag;

import java.util.List;

/**
 * 类名：WeGroupTagService
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
public interface WeGroupTagService extends IService<WeGroupTag> {

    /**
     * 批量插入标签列表
     *
     * @param corpId     企业ID
     * @param groupTagId 群标签组ID
     * @param list       list
     * @return int
     */
    int batchInsert(String corpId, Long groupTagId, List<WeGroupTag> list);

    /**
     * 批量删除标签
     *
     * @param corpId 企业ID
     * @param idList idList
     * @return int
     */
    int delTag(String corpId, List<Long> idList);

    /**
     * 根据groupId批量删除标签
     *
     * @param corpId      企业ID
     * @param groupIdList 标签组ID集合
     * @return int
     */
    int delByGroupId(String corpId, List<Long> groupIdList);
}
