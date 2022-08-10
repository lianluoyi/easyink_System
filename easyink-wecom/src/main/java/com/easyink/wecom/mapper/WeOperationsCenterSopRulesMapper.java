package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeOperationsCenterSopRulesEntity;
import com.easyink.wecom.domain.vo.sop.SopRuleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * sop规则表
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Repository
@Validated
public interface WeOperationsCenterSopRulesMapper extends BaseMapper<WeOperationsCenterSopRulesEntity> {


    /**
     * 批量修改数据
     *
     * @param corpId 企业ID
     * @param list   list
     */
    void batchUpdate(@Param("corpId") String corpId, @Param("list") List<WeOperationsCenterSopRulesEntity> list);

    /**
     * 获取sop规则
     *
     * @param corpId 企业id
     * @param sopId  sopId
     * @param id     规则id
     * @return {@link SopRuleVO}
     */
    SopRuleVO getSopRule(@NotEmpty @Param("corpId") String corpId, @NotNull @Param("sopId") Long sopId, @NotNull @Param("id") Long id);
}
