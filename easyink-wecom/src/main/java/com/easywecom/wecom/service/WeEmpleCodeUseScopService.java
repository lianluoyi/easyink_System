package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeEmpleCodeUseScop;

import java.util.List;

/**
 * 员工活码使用人Service接口
 *
 * @author admin
 * @date 2020-10-04
 */
public interface WeEmpleCodeUseScopService extends IService<WeEmpleCodeUseScop> {
    /**
     * 查询员工活码使用人
     *
     * @param id 员工活码使用人ID
     * @return 员工活码使用人
     */
    WeEmpleCodeUseScop selectWeEmpleCodeUseScopById(Long id);

    /**
     * 查询员工活码使用人列表
     *
     * @param empleCodeId
     * @return 员工活码使用人集合
     */
    List<WeEmpleCodeUseScop> selectWeEmpleCodeUseScopListById(Long empleCodeId, String corpId);

    /**
     * 查询员工活码使用人列表(批量)
     *
     * @param empleCodeIdList 活码id
     * @param corpId 企业id
     * @return list
     */
    List<WeEmpleCodeUseScop> selectWeEmpleCodeUseScopListByIds(List<Long> empleCodeIdList, String corpId);


    /**
     * 新增员工活码使用人
     *
     * @param weEmpleCodeUseScop 员工活码使用人
     * @return 结果
     */
    int insertWeEmpleCodeUseScop(WeEmpleCodeUseScop weEmpleCodeUseScop);

    /**
     * 修改员工活码使用人
     *
     * @param weEmpleCodeUseScop 员工活码使用人
     * @return 结果
     */
    int updateWeEmpleCodeUseScop(WeEmpleCodeUseScop weEmpleCodeUseScop);

    /**
     * 批量删除员工活码使用人
     *
     * @param ids 需要删除的员工活码使用人ID
     * @return 结果
     */
    int deleteWeEmpleCodeUseScopByIds(Long[] ids);

    /**
     * 删除员工活码使用人信息
     *
     * @param id 员工活码使用人ID
     * @return 结果
     */
    int deleteWeEmpleCodeUseScopById(Long id);

    /**
     * 批量保存
     *
     * @param weEmpleCodeUseScops
     * @return
     */
    int batchInsetWeEmpleCodeUseScop(List<WeEmpleCodeUseScop> weEmpleCodeUseScops);


    /**
     * 批量物理删除
     *
     * @param ids
     * @return
     */
    int batchRemoveWeEmpleCodeUseScopIds(List<Long> ids);

    /**
     * 查询员工活码使用部门的信息
     *
     * @param employCodeIdList
     * @return
     */
    List<WeEmpleCodeUseScop> selectDepartmentWeEmpleCodeUseScopListByIds(List<Long> employCodeIdList);
}
