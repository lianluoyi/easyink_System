package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeEmpleCodeMaterial;

import java.util.List;

/**
 * 类名：WeEmpleCodeMaterialService
 *
 * @author 李小琳
 * @date 2021-11-02
 */
public interface WeEmpleCodeMaterialService extends IService<WeEmpleCodeMaterial> {

    /**
     * 批量插入员工活码素材附件
     *
     * @param list list
     * @return int
     */
    int batchInsert(List<WeEmpleCodeMaterial> list);

    /**
     * 根据empleCodeId更新mediaId
     *
     * @param empleCodeId 员工活码ID
     * @param mediaId     群活码ID
     * @return int
     */
    int updateGroupCodeMediaIdByEmpleCodeId(Long empleCodeId, Long mediaId);

    /**
     * 根据emplyCodeId批量删除员工活码附件表
     *
     * @param emplyCodeIdList 员工活码ID列表
     * @return int
     */
    int removeByEmpleCodeId(List<Long> emplyCodeIdList);
}
