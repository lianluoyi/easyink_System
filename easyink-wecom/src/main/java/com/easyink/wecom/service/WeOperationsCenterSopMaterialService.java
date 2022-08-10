package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeOperationsCenterSopMaterialEntity;

import java.util.List;

/**
 * 类名： SOP素材接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
public interface WeOperationsCenterSopMaterialService extends IService<WeOperationsCenterSopMaterialEntity> {

    /**
     * 根据corpId和sopIdList批量删除数据
     *
     * @param corpId    企业ID
     * @param sopIdList sopIdList
     */
    void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList);
}

