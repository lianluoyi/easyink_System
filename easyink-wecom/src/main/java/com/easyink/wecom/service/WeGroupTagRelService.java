package com.easyink.wecom.service;

import com.easyink.wecom.domain.dto.wegrouptag.BatchTagRelDTO;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagRelVO;

import java.util.List;

/**
 * 类名：WeGroupTagRelService
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
public interface WeGroupTagRelService {

    /**
     * 批量给客户群打标签
     *
     * @param batchTagRelDTO batchTagRelDTO
     * @return int
     */
    int batchAddTagRel(BatchTagRelDTO batchTagRelDTO);

    /**
     * 批量给多个客户群移除标签
     *
     * @param batchTagRelDTO batchTagRelDTO
     * @return int
     */
    int batchDelTagRel(BatchTagRelDTO batchTagRelDTO);

    /**
     * 根据tagIdList批量移除与客户群的关联关系
     *
     * @param corpId    企业ID
     * @param tagIdList 标签ID
     * @return int
     */
    int delByTagIdList(String corpId, List<Long> tagIdList);

    /**
     * 查询chatIdList下的关联标签
     *
     * @param corpId     企业ID
     * @param chatIdList 客户群列表
     * @return List<WeGroupTagRelVO>
     */
    List<WeGroupTagRelVO> getByChatIdList(String corpId, List<String> chatIdList);
}
