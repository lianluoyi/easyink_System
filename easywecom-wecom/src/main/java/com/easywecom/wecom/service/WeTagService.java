package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeTag;

import java.util.List;

/**
 * 企业微信标签Service接口
 *
 * @author admin
 * @date 2020-09-07
 */
public interface WeTagService extends IService<WeTag> {

    /**
     * 批量修改客户数据
     * @param list
     */
    void updatWeTagsById(List<WeTag> list);

    /**
     * 保存或修改企业微信标签
     *
     * @param list 标签列表
     */
    void saveOrUpadteWeTag(List<WeTag> list);

    /**
     * 删除企业微信标签信息
     *
     * @param id 企业微信标签ID
     * @return 结果
     */
    int deleteWeTagById(String id, String corpId);

    /**
     * 根据标签id删除标签
     * @param groupIds 标签id
     * @return
     */
    int deleteWeTagByGroupId(String[] groupIds);

    /**
     * 创建标签
     *
     * @param tagId  标签ID
     * @param corpId 企业ID
     */
    void creatTag(String tagId, String corpId);

    /**
     * 感觉标签id删除标签
     * @param tagId 标签id
     */
    void deleteTag(String tagId, String corpId);

    /**
     * 根据标签id修改标签
     * @param tagId 标签id
     */
    void updateTag(String tagId, String corpId);
}
