package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeWordsDetailEntity;
import com.easywecom.wecom.domain.vo.sop.SopAttachmentVO;

import java.util.List;

/**
 * 类名： 话术库附件接口
 *
 * @author 佚名
 * @date 2021/10/25 17:37
 */
public interface WeWordsDetailService extends IService<WeWordsDetailEntity> {
    /**
     * 插入或更新
     *
     * @param wordsDetailEntities 实体列表
     */
    void saveOrUpdate(List<WeWordsDetailEntity> wordsDetailEntities);

    /**
     * 插入或更新 非话术库的附件（朋友圈）
     *
     * @param wordsDetailEntities 附件列表
     * @param isWordsDetail 是否为话术附件
     * @param corpId 企业id
     */
    void saveOrUpdate(List<WeWordsDetailEntity> wordsDetailEntities,Boolean isWordsDetail,String corpId);

    /**
     * 删除
     *
     * @param groupId 主表id
     */
    void deleteByGroupId(Long groupId);

    /**
     * 根据corpId和idList删除话术内容
     *
     * @param corpId 企业ID
     * @param idList idList
     */
    void delByCorpIdAndIdList(String corpId, List<Long> idList);

    /**
     * 通过id查询sop附件
     *
     * @param ruleId ruleId
     * @return {@link List< SopAttachmentVO >}
     */
    List<SopAttachmentVO> listOfRuleId(Long ruleId);

    /**
     * 保存朋友圈任务附件
     *
     * @param attachments 附件
     */
    void saveMomentDetail(List<WeWordsDetailEntity> attachments);
}

