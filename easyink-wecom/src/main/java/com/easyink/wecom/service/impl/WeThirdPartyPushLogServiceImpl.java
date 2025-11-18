package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.WeThirdPartyPushLog;
import com.easyink.wecom.mapper.WeThirdPartyPushLogMapper;
import com.easyink.wecom.service.WeThirdPartyPushLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 第三方推送日志Service业务层处理
 *
 * @author easyink
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeThirdPartyPushLogServiceImpl extends ServiceImpl<WeThirdPartyPushLogMapper, WeThirdPartyPushLog> implements WeThirdPartyPushLogService {


}
