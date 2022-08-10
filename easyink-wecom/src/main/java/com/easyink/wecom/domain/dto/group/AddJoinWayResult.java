package com.easyink.wecom.domain.dto.group;

import com.easyink.wecom.domain.dto.WeResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置客户群进行方式返回结果
 *
 * @author tigger
 * 2022/2/9 15:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddJoinWayResult extends WeResultDTO {
    private String config_id;
}
