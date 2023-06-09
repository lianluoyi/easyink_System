package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeOperationsCenterSopEntity;
import com.easyink.wecom.domain.dto.groupsop.SopBatchSwitchDTO;
import com.easyink.wecom.domain.vo.WeOperationsCenterSopVo;
import com.easyink.wecom.domain.vo.sop.BaseWeOperationsCenterSopVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * SOP基本信息
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Repository
public interface WeOperationsCenterSopMapper extends BaseMapper<WeOperationsCenterSopEntity> {
    /**
     * 获得sop
     * @param corpId 企业id
     * @param sopId sopid 不能为空
     * @return {@link WeOperationsCenterSopVo}
     */
    WeOperationsCenterSopVo getSop(@NotBlank @Param("corpId") String corpId, @NotNull @Param("sopId") Long sopId);

    /**
     * 查询SOP列表
     *
     * @param corpId  企业ID
     * @param sopType sop类型
     * @param name    SOP名称
     * @param isOpen  是否开启
     * @return {@link List<WeOperationsCenterSopVo>}
     */
    List<BaseWeOperationsCenterSopVo> list(@NotBlank @Param("corpId") String corpId, @NotNull @Param("sopType") Integer sopType, @Param("name") String name, @Param("userName") String userName, @Param("isOpen") Integer isOpen);

    /**
     * 批量删除SOP任务
     *
     * @param corpId    企业ID
     * @param sopIdList sopIdList
     */
    void delSopByCorpIdAndSopIdList(@Param("corpId") String corpId, @Param("sopIdList") List<Long> sopIdList);

    /**
     * 批量更新sop开关状态
     *
     * @param switchDTO switchDTO
     */
    void batchSwitch(SopBatchSwitchDTO switchDTO);

    /**
     * 更新SOP基本信息
     *
     * @param corpId     企业ID
     * @param sopId      sopId
     * @param name       SOP名称
     * @param filterType 筛选类型
     */
    void updateSop(@Param("corpId") String corpId, @Param("sopId") Long sopId, @Param("name") String name, @Param("filterType") Integer filterType);
}
