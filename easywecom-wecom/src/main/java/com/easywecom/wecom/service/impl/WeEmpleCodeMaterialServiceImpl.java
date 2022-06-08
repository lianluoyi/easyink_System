package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeEmpleCodeMaterial;
import com.easywecom.wecom.mapper.WeEmpleCodeMaterialMapper;
import com.easywecom.wecom.service.WeEmpleCodeMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类名：WeEmpleCodeMaterialServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-11-02 15:19
 */
@Slf4j
@Service
public class WeEmpleCodeMaterialServiceImpl extends ServiceImpl<WeEmpleCodeMaterialMapper, WeEmpleCodeMaterial> implements WeEmpleCodeMaterialService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<WeEmpleCodeMaterial> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.batchInsert(list);
    }

    @Override
    public int updateGroupCodeMediaIdByEmpleCodeId(Long empleCodeId, Long mediaId) {
        if (empleCodeId == null || mediaId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.updateGroupCodeMediaIdByEmpleCodeId(empleCodeId, mediaId);
    }

    @Override
    public int removeByEmpleCodeId(List<Long> emplyCodeIdList) {
        if (CollectionUtils.isEmpty(emplyCodeIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.removeByEmpleCodeId(emplyCodeIdList);
    }
}
