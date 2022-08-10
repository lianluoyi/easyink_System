package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;
import com.easyink.wecom.domain.dto.group.*;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 客户群「加入群聊」管理
 *
 * @author tigger
 * 2022/2/9 15:02
 **/
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeGroupChatJoinClient {

    @Post(url = "/externalcontact/groupchat/add_join_way")
    AddJoinWayResult addJoinWayConfig(@Body AddJoinWayConfigDTO addDTO, @Header("corpid") String corpId);


    @Post(url = "/externalcontact/groupchat/get_join_way")
    GetJoinWayResult getJoinWayConfig(@Body GetJoinWayConfigDTO getDTO, @Header("corpid") String corpId);


    @Post(url = "/externalcontact/groupchat/update_join_way")
    UpdateJoinWayResult updateJoinWayConfig(@Body UpdateJoinWayConfigDTO updateDTO, @Header("corpid") String corpId);


    @Post(url = "/externalcontact/groupchat/del_join_way")
    DelJoinWayResult delJoinWayConfig(@Body DelJoinWayConfigDTO delDTO, @Header("corpid") String corpId);
}
