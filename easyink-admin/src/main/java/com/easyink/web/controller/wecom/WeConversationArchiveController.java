package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.ConversationArchiveQuery;
import com.easyink.common.core.domain.ConversationArchiveViewContextDTO;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.vo.ConversationArchiveVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeConversationArchiveService;
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
    public AjaxResult<PageInfo<ConversationArchiveVO>> getChatContactList(ConversationArchiveQuery query) {
        if (StringUtils.isEmpty(LoginTokenService.getLoginUser().getCorpId())){
            return AjaxResult.success(new ArrayList<>());
        }
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weConversationArchiveService.getChatContactList(query));
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
    public AjaxResult<PageInfo<ConversationArchiveVO>> getChatRoomContactList(ConversationArchiveQuery query) {
        if (StringUtils.isEmpty(LoginTokenService.getLoginUser().getCorpId())){
            return AjaxResult.success(new ArrayList<>());
        }
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weConversationArchiveService.getChatRoomContactList(query));
    }


    /**
     * 获取全局会话数据接口
     *
     * @param query 入参
     * @return
     */
    @ApiOperation(value = "获取全局会话数据接口", httpMethod = "GET")
    @GetMapping("/getChatAllList")
    public AjaxResult<PageInfo<ConversationArchiveVO>> getChatAllList(ConversationArchiveQuery query) {
        if (StringUtils.isEmpty(LoginTokenService.getLoginUser().getCorpId())){
            return AjaxResult.success(new ArrayList<>());
        }
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weConversationArchiveService.getChatAllList(query, tokenService.getLoginUser(ServletUtils.getRequest())));
    }

    @ApiOperation(value = "查看消息上下文接口", httpMethod = "GET")
    @GetMapping("/view/context")
    public TableDataInfo<ConversationArchiveVO> viewContext(ConversationArchiveViewContextDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return weConversationArchiveService.viewContext(dto);
    }
}
