package com.easyink.wecom.handler.shorturl;

import com.alibaba.fastjson.JSON;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import com.easyink.common.shorturl.model.BaseShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.common.shorturl.service.IShortUrlHandler;
import com.easyink.common.shorturl.service.impl.ShortUrlServiceImpl;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类名: 抽象短链处理类
 * <p>
 * 继承此类 需要实现 {@link AbstractShortUrlHandler#shortUrlType()} 设置处理类的短链处理类型,详见 {@link ShortUrlTypeEnum}
 * {@link AbstractShortUrlHandler#getUrlByMapping(String)} 进行对短链不同的业务处理
 * 默认取公众号的域名,如果有要修改短链域名,则可以重写 {@link AbstractShortUrlHandler#getDomain(String, String)} 方法
 *
 * @author : silver_chariot
 * @date : 2023/3/14 15:44
 **/
@Component
@Slf4j
public abstract class AbstractShortUrlHandler<T extends BaseShortUrlAppendInfo> extends ShortUrlServiceImpl implements IShortUrlHandler<T>, InitializingBean {

    @Autowired
    private WechatOpenService wechatOpenService;
    @Autowired
    private ShortUrlHandlerFactory shortUrlHandlerFactory ;


    @Override
    public String createShortUrl(String corpId, String url, String createBy, T appendInfo) {
        if (StringUtils.isAnyBlank(url, corpId)) {
            return StringUtils.EMPTY;
        }
        String appInfoStr = JSON.toJSONString(appendInfo);
        // 生成附加信息
        String shortCode = createShortCode(url, shortUrlType().getType(), createBy, appInfoStr);
        // 生成短链,目前短链都是默认使用公众号的域名
        return genShortUrl(getDomain(corpId, appInfoStr), shortCode);
    }

    @Override
    public String handleAndGetRedirectUrl(SysShortUrlMapping mapping) {
        // 根据短链code获取原链接映射详情
        if (mapping == null || StringUtils.isBlank(mapping.getLongUrl())) {
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_PAGE);
        }
        return handleByMapping(mapping);
    }

    /**
     * 根据锻炼映射处理短链,并返回需要重定向的url
     *
     * @param mapping 短链->长链映射结果 {@link SysShortUrlMapping}
     * @return 需要重定向的url
     */
    protected abstract String handleByMapping(SysShortUrlMapping mapping);



    /**
     * 获取处理的短链类型,实现类需要重写
     *
     * @return {@link ShortUrlTypeEnum} 短链类型
     */
    public abstract ShortUrlTypeEnum shortUrlType();

    /**
     * 获取短链的域名,默认为公众号配置的域名,如果有其他需求可以重写此方法
     *
     * @param appendInfo 短链附件信息 , 需继承 {@link com.easyink.common.shorturl.model.BaseShortUrlAppendInfo}
     * @param corpId     企业id
     * @return 短链域名
     */
    public String getDomain(String corpId, String appendInfo) {
        return wechatOpenService.getDomain(corpId);
    }

    /**
     * 通过短链获取公众号配置
     *
     * @param mapping {@link SysShortUrlMapping}
     * @return WeOpenConfig
     */
    public abstract WeOpenConfig getWeOpenConfig(SysShortUrlMapping mapping);

    @Override
    public void afterPropertiesSet() {
        shortUrlHandlerFactory.register(shortUrlType().getType(), this);
    }
}
