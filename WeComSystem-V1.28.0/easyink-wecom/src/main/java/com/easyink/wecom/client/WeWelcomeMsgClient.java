package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgAddDTO;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgDeleteDTO;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgResult;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgUpdateDTO;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 入群欢迎语素材管理
 *
 * @author: hu
 * @date: 2021-08-18 17:08
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeWelcomeMsgClient {

    @Post(url = "/externalcontact/group_welcome_template/add")
    GroupWelcomeMsgResult add(@Body GroupWelcomeMsgAddDTO groupWelcomeMsgAddDTO, @Header("corpid") String corpId);

    @Post(url = "/externalcontact/group_welcome_template/del")
    GroupWelcomeMsgResult del(@Body GroupWelcomeMsgDeleteDTO welcomeMsgUpdateDTO, @Header("corpid") String corpId);

    @Post(url = "/externalcontact/group_welcome_template/edit")
    GroupWelcomeMsgResult edit(@Body GroupWelcomeMsgUpdateDTO welcomeMsgUpdateDTO, @Header("corpid")String corpId);
}
