package com.easywecom.wecom.utils;

import cn.hutool.core.util.StrUtil;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.enums.MessageType;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.message.TextMessageDTO;
import com.easywecom.wecom.service.WeCorpAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

/**
 * 类名： ApplicationMessageUtil
 *
 * @author 佚名
 * @date 2021/11/30 19:28
 */
@Component
@Slf4j
@Validated
public class ApplicationMessageUtil {
    private final WeCorpAccountService corpAccountService;
    private final WeMessagePushClient messagePushClient;

    @Value("${wecome.authorizeUrl}")
    private String authorizeUrl;

    @Autowired
    public ApplicationMessageUtil(WeCorpAccountService corpAccountService, WeMessagePushClient messagePushClient) {
        this.corpAccountService = corpAccountService;
        this.messagePushClient = messagePushClient;
    }

    /**
     * 发送应用消息（文本）
     *
     * @param userIds  员工id列表
     * @param corpId   企业id
     * @param msg      消息模板 例如 “员工姓名：{0},年龄：{1}”
     * @param paramMsg 替换占位符{}消息
     */
    public void sendAppMessage(@NotEmpty List<String> userIds, String corpId, String msg, String... paramMsg) {
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(corpId);
        String agentId = validWeCorpAccount.getAgentId();
        // 文本消息
        TextMessageDTO text = new TextMessageDTO();
        //设置发送者 发送给企业员工
        String userString = getUserString(userIds);
        pushDto.setTouser(userString);
        text.setContent(MessageFormat.format(msg, paramMsg));
        pushDto.setAgentid(Integer.valueOf(agentId));
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        log.info("发送应用消息：toUser:{},corpId:{}", userString, corpId);
        messagePushClient.sendMessageToUser(pushDto, agentId, corpId);
    }

    /**
     * 用'|'(竖杠)拼接员工id
     *
     * @param userIds 员工id
     * @return 员工id
     */
    private static String getUserString(List<String> userIds) {
        String userString = StrUtil.EMPTY;
        if (CollectionUtils.isNotEmpty(userIds)) {
            userString = String.join(WeConstans.VERTICAL_BAR, userIds);
        }
        return userString;
    }

    /**
     * 获取详情链接
     *
     * @param corpId            企业ID
     * @param communityTaskType 社群运营H5列表页数据类型
     * @return String
     */
    public String getUrlContent(String corpId, Integer communityTaskType) {
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(corpId);
        String agentId = validWeCorpAccount.getAgentId();
        String authorizeRedirectUrl = validWeCorpAccount.getH5DoMainName() + "/#/task";
        String redirectUrl = null;
        try {
            redirectUrl = URLEncoder.encode(String.format("%s?corpId=%s&agentId=%s&type=%s", authorizeRedirectUrl, corpId, agentId, communityTaskType), "UTF-8");
        } catch (
                UnsupportedEncodingException e) {
            log.error("获取tag的redirectUrl失败：ex：{}", ExceptionUtils.getStackTrace(e));
        }
        String context = String.format(
                "<a href='%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect'>点击查看详情</a>",
                authorizeUrl, corpId, redirectUrl, corpId);
        return context;
    }


    /**
     * 获取个人朋友圈链接
     * @return 链接
     */
    public String getMomentUrl(String corpId,Long momentTaskId){
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(corpId);
        String authorizeRedirectUrl = validWeCorpAccount.getH5DoMainName() + "/#/friendCircle";
        String redirectUrl = null;
        try {
            redirectUrl = URLEncoder.encode(String.format("%s?momentTaskId=%s", authorizeRedirectUrl,momentTaskId), "UTF-8");
        } catch (
                UnsupportedEncodingException e) {
            log.error("获取tag的redirectUrl失败：ex：{}", ExceptionUtils.getStackTrace(e));
        }
        return String.format(
                "<a href='%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect'>前往发布</a>",
                authorizeUrl, corpId, redirectUrl, corpId);
    }
}
