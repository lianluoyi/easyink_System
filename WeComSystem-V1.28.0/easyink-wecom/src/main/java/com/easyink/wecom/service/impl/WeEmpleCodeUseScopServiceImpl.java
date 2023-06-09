package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.easyink.wecom.mapper.WeEmpleCodeUseScopMapper;
import com.easyink.wecom.service.WeEmpleCodeUseScopService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工活码使用人Service业务层处理
 *
 * @author admin
 * @date 2020-10-04
 */
@Service
public class WeEmpleCodeUseScopServiceImpl extends ServiceImpl<WeEmpleCodeUseScopMapper, WeEmpleCodeUseScop> implements WeEmpleCodeUseScopService {
    @Autowired
    private WeEmpleCodeUseScopMapper weEmpleCodeUseScopMapper;

    /**
     * 查询员工活码使用人
     *
     * @param id 员工活码使用人ID
     * @return 员工活码使用人
     */
    @Override
    public WeEmpleCodeUseScop selectWeEmpleCodeUseScopById(Long id) {
        return weEmpleCodeUseScopMapper.selectWeEmpleCodeUseScopById(id);
    }

    /**
     * 查询员工活码使用人列表
     *
     * @param empleCodeId
     * @param corpId 企业id
     * @return {@link List<  WeEmpleCodeUseScop  >}
     */
    @Override
    public List<WeEmpleCodeUseScop> selectWeEmpleCodeUseScopListById(Long empleCodeId, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return weEmpleCodeUseScopMapper.selectWeEmpleCodeUseScopListById(empleCodeId, corpId);
    }

    /**
     * 查询员工活码使用人列表(批量)
     *
     * @param empleCodeIdList 活码id
     * @param corpId 企业id
     * @return {@link List<  WeEmpleCodeUseScop  >}
     */
    @Override
    public List<WeEmpleCodeUseScop> selectWeEmpleCodeUseScopListByIds(List<Long> empleCodeIdList, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return weEmpleCodeUseScopMapper.selectWeEmpleCodeUseScopListByIds(empleCodeIdList, corpId);
    }

    /**
     * 新增员工活码使用人
     *
     * @param weEmpleCodeUseScop 员工活码使用人
     * @return 结果
     */
    @Override
    public int insertWeEmpleCodeUseScop(WeEmpleCodeUseScop weEmpleCodeUseScop) {
        return weEmpleCodeUseScopMapper.insertWeEmpleCodeUseScop(weEmpleCodeUseScop);
    }

    /**
     * 修改员工活码使用人
     *
     * @param weEmpleCodeUseScop 员工活码使用人
     * @return 结果
     */
    @Override
    public int updateWeEmpleCodeUseScop(WeEmpleCodeUseScop weEmpleCodeUseScop) {
        return weEmpleCodeUseScopMapper.updateWeEmpleCodeUseScop(weEmpleCodeUseScop);
    }

    /**
     * 批量删除员工活码使用人
     *
     * @param ids 需要删除的员工活码使用人ID
     * @return 结果
     */
    @Override
    public int deleteWeEmpleCodeUseScopByIds(Long[] ids) {
        return weEmpleCodeUseScopMapper.deleteWeEmpleCodeUseScopByIds(ids);
    }

    /**
     * 删除员工活码使用人信息
     *
     * @param id 员工活码使用人ID
     * @return 结果
     */
    @Override
    public int deleteWeEmpleCodeUseScopById(Long id) {
        return weEmpleCodeUseScopMapper.deleteWeEmpleCodeUseScopById(id);
    }


    /**
     * 批量保存
     *
     * @param weEmpleCodeUseScops
     * @return
     */
    @Override
    public int batchInsetWeEmpleCodeUseScop(List<WeEmpleCodeUseScop> weEmpleCodeUseScops) {
        return weEmpleCodeUseScopMapper.batchInsetWeEmpleCodeUseScop(weEmpleCodeUseScops);
    }

    /**
     * 批量逻辑删除
     *
     * @param ids
     * @return
     */
    @Override
    public int batchRemoveWeEmpleCodeUseScopIds(List<Long> ids) {
        return weEmpleCodeUseScopMapper.batchRemoveWeEmpleCodeUseScopIds(ids);
    }

    /**
     * 查询员工活码使用部门的信息
     *
     * @param employCodeIdList
     * @return
     */
    @Override
    public List<WeEmpleCodeUseScop> selectDepartmentWeEmpleCodeUseScopListByIds(List<Long> employCodeIdList) {
        if (CollectionUtils.isNotEmpty(employCodeIdList)) {
            return weEmpleCodeUseScopMapper.selectDepartmentWeEmpleCodeUseScopListByIds(employCodeIdList);
        }
        return new ArrayList<>();
    }
}
