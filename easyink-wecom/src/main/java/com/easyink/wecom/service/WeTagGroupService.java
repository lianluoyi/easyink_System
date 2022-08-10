package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.dto.tag.WeCropGroupTagDTO;

import java.util.List;

/**
 * 标签组Service接口
 *
 * @author admin
 * @date 2020-09-07
 */
public interface WeTagGroupService extends IService<WeTagGroup> {


    /**
     * 查询标签组列表
     *
     * @param weTagGroup 标签组
     * @return 标签组集合
     */
    List<WeTagGroup> selectWeTagGroupList(WeTagGroup weTagGroup);

    /**
     * 新增标签组
     *
     * @param weTagGroup 标签组
     * @return 结果
     */
    void insertWeTagGroup(WeTagGroup weTagGroup);

    /**
     * 修改标签组
     *
     * @param weTagGroup 标签组
     * @return 结果
     */
    void updateWeTagGroup(WeTagGroup weTagGroup);

    /**
     * 批量删除标签组
     *
     * @param ids 需要删除的标签组ID
     * @return 结果
     */
    int deleteWeTagGroupByIds(String[] ids, String corpId);


    /**
     * 同步标签
     *
     * @return
     */
    void synchWeTags(String corpId);


    /**
     * 来自微信端批量保存或者更新标签组和标签
     *
     * @param tagGroup
     * @param isSync
     */
    void batchSaveOrUpdateTagGroupAndTag(List<WeCropGroupTagDTO> tagGroup, Boolean isSync, String corpId);

    /**
     * 创建标签组
     * @param id 标签id
     * @param corpId 企业id
     */
    void createTagGroup(String id, String corpId);

    /**
     * 删除标签组
     * @param id 标签id
     * @param corpId 企业id
     */
    void deleteTagGroup(String id, String corpId);

    /**
     * 修改标签组id
     * @param id 标签组id
     */
    void updateTagGroup(String id, String corpId);


    /**
     * 根据企业员工与添加客户关系获取标签列表
     *
     * @param flowerCustomerRelId
     * @return 标签组列表
     */
    List<WeTagGroup> findCustomerTagByFlowerCustomerRelId(String flowerCustomerRelId);
}
