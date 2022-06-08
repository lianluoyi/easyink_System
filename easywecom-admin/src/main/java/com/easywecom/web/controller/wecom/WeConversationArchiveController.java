package com.easywecom.web.controller.wecom;

import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.ConversationArchiveQuery;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.token.TokenService;
import com.easywecom.common.utils.ServletUtils;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.vo.ConversationArchiveVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeConversationArchiveService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author admin
 * @description 会话存档controller
 * @date 2020/12/19 13:51
 **/
@Api(tags = "会话存档controller")
@Slf4j
@RestController
@RequestMapping("/wecom/finance")
public class WeConversationArchiveController extends BaseController {
    @Autowired
    private WeConversationArchiveService weConversationArchiveService;
    @Autowired
    private TokenService tokenService;

    /**
     * 获取单聊会话数据接口
     *
     * @param query      入参
     */
    @ApiOperation(value = "获取单聊会话数据接口", httpMethod = "GET")
    @GetMapping("/getChatContactList")
    public TableDataInfo<PageInfo<ConversationArchiveVO>> getChatContactList(ConversationArchiveQuery query) {
        if (StringUtils.isEmpty(LoginTokenService.getLoginUser().getCorpId())){
            return getDataTable(new ArrayList<>());
        }
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weConversationArchiveService.getChatContactList(query));
    }


    /**
     * 获取群聊会话数据接口
     *
     * @param query   入参
     * @param /fromId 发送人id
     * @param /room   接收人id
     * @return
     */
    @ApiOperation(value = "获取群聊会话数据接口", httpMethod = "GET")
    @GetMapping("/getChatRoomContactList")
    public TableDataInfo<PageInfo<ConversationArchiveVO>> getChatRoomContactList(ConversationArchiveQuery query) {
        if (StringUtils.isEmpty(LoginTokenService.getLoginUser().getCorpId())){
            return getDataTable(new ArrayList<>());
        }
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weConversationArchiveService.getChatRoomContactList(query));
    }


    /**
     * 获取全局会话数据接口
     *
     * @param query 入参
     * @return
     */
    @ApiOperation(value = "获取全局会话数据接口", httpMethod = "GET")
    @GetMapping("/getChatAllList")
    public TableDataInfo<PageInfo<ConversationArchiveVO>> getChatAllList(ConversationArchiveQuery query) {
        if (StringUtils.isEmpty(LoginTokenService.getLoginUser().getCorpId())){
            return getDataTable(new ArrayList<>());
        }
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weConversationArchiveService.getChatAllList(query, tokenService.getLoginUser(ServletUtils.getRequest())));
    }

}
