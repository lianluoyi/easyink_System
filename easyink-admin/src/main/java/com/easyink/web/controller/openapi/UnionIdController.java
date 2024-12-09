package com.easyink.web.controller.openapi;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.utils.wecom.CorpSecretDecryptUtil;
import com.easyink.wecom.domain.dto.unionid.GetUnionIdDTO;
import com.easyink.wecom.domain.vo.unionid.GetUnionIdVO;
import com.easyink.wecom.service.WeCustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: easyink获取unionId 接口
 *
 * @author : silver_chariot
 * @date : 2023/1/4 14:19
 **/
@RestController
@RequestMapping("/unionId")
@AllArgsConstructor
public class UnionIdController extends BaseController {

    private final WeCustomerService weCustomerService;

    @PostMapping("/getByExternalUserId")
    public AjaxResult<GetUnionIdVO> getByExternalUserId(@RequestBody GetUnionIdDTO getUnionIdDTO) {
        return AjaxResult.success( weCustomerService.getDetailByExternalUserId(getUnionIdDTO) );
    }

}
