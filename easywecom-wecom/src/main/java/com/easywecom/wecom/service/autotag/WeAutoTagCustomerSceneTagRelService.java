package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.dto.autotag.customer.CustomerSceneDTO;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagCustomerSceneTagRel;

import java.util.List;

/**
 * 新客标签场景与标签关系表(WeAutoTagCustomerSceneTagRel)表服务接口
 *
 * @author tigger
 * @since 2022-02-27 15:52:32
 */
public interface WeAutoTagCustomerSceneTagRelService extends IService<WeAutoTagCustomerSceneTagRel> {

    /**
     * 批量添加
     *
     * @param toWeAutoTagCustomerSceneTagRelList
     * @return
     */
    int batchSave(List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList);

    /**
     * 批量添加或修改
     *
     * @param toWeAutoTagCustomerSceneTagRelList
     * @param insertOnly
     * @return
     */
    int batchSave(List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList, Boolean insertOnly);

    /**
     * 修改
     *
     * @param customerSceneList
     * @param toWeAutoTagCustomerSceneTagRelList
     */
    int edit(List<CustomerSceneDTO> customerSceneList, List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList);

    /**
     * 删除场景标签根据场景列表
     *
     * @param removeSceneIdList
     */
    int removeBySceneIdList(List<Long> removeSceneIdList);

    /**
     * 删除指定场景下的标签列表
     *
     * @param customerSceneId
     * @param removeTagIdList
     * @return
     */
    int removeBySceneIdAndTagIdList(Long customerSceneId, List<String> removeTagIdList);

    /**
     * 根据规则id列表删除新客场景标签信息
     *
     * @param removeRuleIdList
     * @return
     */
    int removeByRuleIdList(List<Long> removeRuleIdList);

    /**
     * 获取场景下的标签列表
     *
     * @param sceneIdList
     * @return
     */
    List<WeTag> getTagListBySceneIdList(List<Long> sceneIdList);
}

