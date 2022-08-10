package com.easywecom.wecom.service;

import com.easywecom.wecom.domain.dto.groupsop.AddWeGroupSopDTO;
import com.easywecom.wecom.domain.dto.groupsop.DelWeGroupSopDTO;
import com.easywecom.wecom.domain.dto.groupsop.UpdateWeSopDTO;

/**
 * 类名：WeGroupSopV2Service
 *
 * @author Society my sister Li
 * @date 2021-11-30 14:55
 */
public interface WeGroupSopV2Service {

    /**
     * 添加SOP
     *
     * @param addWeGroupSopDTO addWeGroupSopDTO
     */
    void addSop(AddWeGroupSopDTO addWeGroupSopDTO);

    /**
     * 删除SOP
     *
     * @param delWeGroupSopDTO delWeGroupSopDTO
     */
    void delSop(DelWeGroupSopDTO delWeGroupSopDTO);

    /**
     * 修改SOP信息
     *
     * @param updateWeSopDTO updateWeSopDTO
     */
    void update(UpdateWeSopDTO updateWeSopDTO);
}
