package com.easyink.wecom.openapi.service.impl;

import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.openapi.service.ThirdService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 第三方服务的员工service impl
 * @author tigger
 * 2024/11/25 17:01
 **/
@AllArgsConstructor
@Service
public class ThirdServiceImpl implements ThirdService {
    private final WeUserMapper weUserMapper;
    private final WeCustomerMapper weCustomerMapper;

    @Override
    public List<String> listUserIdByCorpId(String corpId) {
        return weUserMapper.selectByCorpId(corpId);
    }

    @Override
    public List<String> listExternalUserIdByCorpId(String corpId) {
        return weCustomerMapper.selectExternalUserIdByCorpId(corpId);
    }
}
