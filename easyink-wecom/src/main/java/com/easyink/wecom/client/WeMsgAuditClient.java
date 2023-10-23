package com.easyink.wecom.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Post;
import com.easyink.common.exception.RetryException;
import com.easyink.wecom.client.retry.EnableRetry;
import com.easyink.wecom.domain.dto.msgaudit.WeMsgAuditDTO;
import com.easyink.wecom.domain.vo.WeMsgAuditVO;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 会话存档接口
 *
 * @author: 1*+
 * @date: 2021-08-18 17:10
 */
@Component
@EnableRetry(retryExceptionClass = RetryException.class)
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeMsgAuditClient {



    /**
     * 获取会话内容存档开启成员列表
     */
    @Post(url = "/msgaudit/get_permit_user_list")
    WeMsgAuditDTO getPermitUserList(@Body WeMsgAuditDTO msgAuditDto, @Header("corpid") String corpId);

    /**
     * 单聊 获取会话中外部成员的同意情况
     *
     * @param msgAuditDto
     * @return
     */
    @Post(url = "/msgaudit/check_single_agree")
    WeMsgAuditDTO checkSingleAgree(@Body WeMsgAuditDTO msgAuditDto, @Header("corpid") String corpId);

    /**
     * 群聊 获取群会话中外部成员的同意情况
     *
     * @param weMsgAuditVo
     * @return
     */
    @Post(url = "/msgaudit/check_room_agree")
    WeMsgAuditDTO checkRoomAgree(@Body WeMsgAuditVO weMsgAuditVo, @Header("corpid") String corpId);

    /**
     * 获取会话内容存档内部群信息
     *
     * @param weMsgAuditVo
     * @return
     */
    @Post(url = "/msgaudit/groupchat/get")
    WeMsgAuditDTO getGroupChat(@Body WeMsgAuditVO weMsgAuditVo, @Header("corpid") String corpId);
}
