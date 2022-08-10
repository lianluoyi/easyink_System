package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeOperationsCenterSopRulesEntity;
import com.easywecom.wecom.domain.dto.groupsop.AddWeOperationsCenterSopRuleDTO;
import com.easywecom.wecom.domain.vo.sop.SopRuleVO;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名： sop规则表接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Validated
public interface WeOperationsCenterSopRulesService extends IService<WeOperationsCenterSopRulesEntity> {

    /**
     * 批量保存规则和规则下的素材列表
     *
     * @param sopId    sopId
     * @param corpId   企业ID
     * @param ruleList sop规则列表
     */
    void batchSaveRuleAndMaterialList(Long sopId, String corpId, List<AddWeOperationsCenterSopRuleDTO> ruleList);

    /**
     * 根据corpId和sopIdList批量删除数据
     *
     * @param corpId    企业ID
     * @param sopIdList sopIdList
     */
    void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList);

    /**
     * 更新规则
     *
     * @param corpId   企业ID
     * @param sopId    sopId
     * @param ruleList 新增或修改规则
     * @param delList  需要删除的规则
     */
    void updateSopRules(String corpId, Long sopId, List<AddWeOperationsCenterSopRuleDTO> ruleList, List<Long> delList);

    /**
     * 获取sop规则
     *
     * @param corpId 企业id
     * @param sopId  sopId
     * @param id     规则id
     * @return {@link SopRuleVO}
     */
    SopRuleVO getSopRule(@NotEmpty String corpId, @NotNull Long sopId, @NotNull Long id);
}

