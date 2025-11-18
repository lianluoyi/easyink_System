package com.easyink.wecom.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import com.easyink.common.shorturl.model.BaseShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.handler.shorturl.AbstractShortUrlHandler;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 类名: 客户专属员工活码短链处理
 *
 * @author : tigger
 * @date : 2025/1/13 14:40
 **/
@Component("customerEmployCodeShortUrlHandler")
@RequiredArgsConstructor
@Slf4j
public class CustomerEmployCodeShortUrlHandler<T extends BaseShortUrlAppendInfo> extends AbstractShortUrlHandler<T> {
    private final WechatOpenService wechatOpenService;

    @Override
    protected String handleByMapping(SysShortUrlMapping mapping) {
        return mapping.getLongUrl();
    }

    @Override
    public ShortUrlTypeEnum shortUrlType() {
        return ShortUrlTypeEnum.CUSTOMER_USER_CODE;
    }

    /**
     * 通过短链获取公众号配置
     *
     * @param shortCode {@link SysShortUrlMapping}
     * @return WeOpenConfig
     */
    @Override
    public WeOpenConfig getWeOpenConfig(SysShortUrlMapping shortCode) {
        if (shortCode == null) {
            throw new CustomException(ResultTip.TIP_APPEND_MISSING);
        }
        String corpId = shortCode.getEmpleAppendInfoCorpId();
        if (corpId == null) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        WeOpenConfig config = wechatOpenService.getOne(new LambdaQueryWrapper<WeOpenConfig>().eq(WeOpenConfig::getCorpId, corpId).last(GenConstants.LIMIT_1));
        if (config == null) {
            throw new CustomException(ResultTip.TIP_NO_OFFICAIL_ACCOUNT_CONFIG);
        }
        return config;
    }


}
