package com.easywecom.wecom.domain.dto.group;

import com.easywecom.wecom.domain.WeGroupCodeActual;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新
 *
 * @author tigger
 * 2022/2/9 15:42
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJoinWayConfigDTO extends AddJoinWayConfigDTO{

    private String config_id;

    @Override
    public UpdateJoinWayConfigDTO copyFromWeGroupCodeActual(WeGroupCodeActual corpActual) {
        UpdateJoinWayConfigDTO updateDTO = (UpdateJoinWayConfigDTO) super.copyFromWeGroupCodeActual(corpActual);
        updateDTO.setConfig_id(corpActual.getConfigId());
        return updateDTO;
    }
}
