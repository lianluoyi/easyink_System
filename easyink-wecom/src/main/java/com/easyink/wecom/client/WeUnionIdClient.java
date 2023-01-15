package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Query;
import com.easyink.wecom.domain.dto.customer.ExternalUserDetail;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import com.easyink.wecom.interceptor.WeUnionIdInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 获取unionid的http客户端
 *
 * @author : silver_chariot
 * @date : 2023/1/5 14:03
 **/
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeUnionIdInterceptor.class)
public interface WeUnionIdClient {
    /**
     * 根据外部联系人userId 获取unionId详情
     *
     * @param externalUserId 外部联系人userid
     * @return {@link ExternalUserDetail }
     */
    @Get(url = "/externalcontact/get")
    ExternalUserDetail getByExternalUserId(@Query("external_userid") String externalUserId,
                                           @Header("corpId") String corpId,
                                           @Header("corpSecret") String corpSecret);
}
