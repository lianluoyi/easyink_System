package com.easyink.wecom.domain.dto.message;

import com.easyink.wecom.domain.dto.WeResultDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class QueryCustomerMessageStatusResultDTO extends WeResultDTO {

    /**
     * 返回查询信息列表
     */
    private List<DetailMessageStatusResultDTO> detail_list;

    private String check_status;


}


