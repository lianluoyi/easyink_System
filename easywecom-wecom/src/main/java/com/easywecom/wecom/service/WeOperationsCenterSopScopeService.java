package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeOperationsCenterSopScopeEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名： SOP作用范围接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:22
 */
@Validated
public interface WeOperationsCenterSopScopeService extends IService<WeOperationsCenterSopScopeEntity> {

    /**
     * 根据corpId和sopIdList删除数据
     *
     * @param corpId    企业ID
     * @param sopIdList sopIdList
     */
    void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList);

    /**
     * 更新数据
     *
     * @param corpId            cropId
     * @param sopId             sopId
     * @param userIdList        员工Id列表
     * @param departmentIdList  部门Id列表
     */
    void updateSopScope(String corpId, Long sopId, List<String> userIdList, List<String> departmentIdList);

    /**
     * 查询SOP任务下的作用数据
     *
     * @param sopId  sopId
     * @param corpId 企业ID
     * @return List<WeOperationsCenterSopScopeEntity>
     */
    List<WeOperationsCenterSopScopeEntity> getScope(@NotNull Long sopId, @NotEmpty String corpId);

    /**
     * 批量新增数据
     *
     * @param list list
     */
    void batchSave(List<WeOperationsCenterSopScopeEntity> list);
}

