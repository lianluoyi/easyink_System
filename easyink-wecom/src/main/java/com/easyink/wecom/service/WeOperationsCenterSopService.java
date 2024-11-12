package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeOperationsCenterSopEntity;
import com.easyink.wecom.domain.dto.customersop.EditUserDTO;
import com.easyink.wecom.domain.dto.groupsop.SopBatchSwitchDTO;
import com.easyink.wecom.domain.vo.sop.BaseWeOperationsCenterSopVo;
import com.easyink.wecom.domain.vo.sop.SopDetailVO;

import java.util.List;

/**
 * 类名： SOP基本信息接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
public interface WeOperationsCenterSopService extends IService<WeOperationsCenterSopEntity> {

    /**
     * 查询sop列表
     *
     * @param corpId   企业id
     * @param sopType  sop类型
     * @param name     sop名称
     * @param userName 员工名称
     * @param isOpen   是否开启
     * @return List<BaseWeOperationsCenterSopVo>
     */
    List<BaseWeOperationsCenterSopVo> list(String corpId, Integer sopType, String name, String userName, Integer isOpen);

    /**
     * 批量删除sop任务
     *  @param corpId    企业ID
     * @param sopIdList sopIdList
     * @param sopType
     */
    void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList, Integer sopType);

    /**
     * 批量更新sop开关状态
     *
     * @param switchDTO switchDTO
     */
    void batchSwitch(SopBatchSwitchDTO switchDTO);

    /**
     * 编辑SOP基本信息
     *
     * @param corpId     企业ID
     * @param sopId      sopId
     * @param name       sop名称
     * @param filterType 筛选类型
     */
    void updateSop(String corpId, Long sopId, String name, Integer filterType);

    /**
     * 查询开启任务的SOP
     *
     * @return List<WeOperationsCenterSopEntity>
     */
    List<WeOperationsCenterSopEntity> findSwitchOpenSop();

    /**
     * 查询sop详情
     *
     * @param sopId  sopid
     * @param corpId 企业id
     * @return {@link List<SopDetailVO>}
     */
    SopDetailVO listOfDetail(Long sopId, String corpId);

    /**
     * 修改使用员工
     *
     * @param updateWeSopDTO 参数
     */
    void editUser(EditUserDTO updateWeSopDTO);
}

