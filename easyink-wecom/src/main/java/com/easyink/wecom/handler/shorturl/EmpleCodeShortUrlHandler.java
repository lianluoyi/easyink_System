package com.easyink.wecom.handler.shorturl;

import com.alibaba.fastjson.JSON;
import com.easyink.common.config.WechatOpenConfig;
import com.easyink.common.constant.miniapp.MiniAppConst;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import com.easyink.common.shorturl.model.BaseShortUrlAppendInfo;
import com.easyink.common.shorturl.model.EmpleCodeShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.wecom.client.WechatOpenClient;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.req.GenerateUrlLinkReq;
import com.easyink.wecom.domain.resp.GenerateUrlLinkResp;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 类名: 员工活码短链处理
 *
 * @author : silver_chariot
 * @date : 2023/3/14 17:40
 **/
@Component("empleCodeShortUrlHandler")
@RequiredArgsConstructor
@Slf4j
public class EmpleCodeShortUrlHandler<T extends BaseShortUrlAppendInfo> extends AbstractShortUrlHandler<T> {
    private final WechatOpenConfig wechatOpenConfig;
    private final WechatOpenClient wechatOpenClient;
    private final WeEmpleCodeMapper weempleCodeMapper;

    @Override
    protected String handleByMapping(SysShortUrlMapping mapping) {
        if (mapping == null || StringUtils.isBlank(mapping.getLongUrl())) {
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_LONG_URL);
        }
        // 获取系统配置的小程序配置（目前配置在配置文件中）
        if (wechatOpenConfig == null || wechatOpenConfig.getMiniApp() == null || StringUtils.isAnyBlank(wechatOpenConfig.getMiniApp()
                                                                                                                        .getAppId(), wechatOpenConfig.getMiniApp()
                                                                                                                                                     .getAppSecret())) {
            throw new CustomException(ResultTip.TIP_NO_MINI_APP_CONFIG);
        }
        // 生成唤醒微信小程序的跳转link
        GenerateUrlLinkReq req = new GenerateUrlLinkReq().path(getMiniAppPagePath())
                                                         .query(genQueryString(mapping))
                                                         .buildReq();
        GenerateUrlLinkResp resp = wechatOpenClient.generateUrlLink(req);
        if (resp == null || StringUtils.isBlank(resp.getUrl_link())) {
            log.error("[活码小程序]生成小程序的跳转link失败,mapping:{},req:{},resp:{}", mapping, req, resp);
            throw new CustomException(ResultTip.TIP_ERROR_CREATE_MINI_APP_URL_LINK);
        }
        return resp.getUrl_link();
    }

    @Override
    public ShortUrlTypeEnum shortUrlType() {
        return ShortUrlTypeEnum.USER_CODE;
    }

    @Override
    public WeOpenConfig getWeOpenConfig(SysShortUrlMapping shortCode) {
        throw new CustomException(ResultTip.TIP_GENERAL_NOT_FOUND);
    }

    /**
     * 获取小程序的page路径
     *
     * @return 小程序的page路径
     */
    protected String getMiniAppPagePath() {
        return MiniAppConst.USER_CODE_PAGE_PATH;
    }

    /**
     * 拼接query字符串
     *
     * @param mapping 长链 短链 映射{@link SysShortUrlMapping }
     * @return 传到小程序page 的queryString
     */
    protected String genQueryString(SysShortUrlMapping mapping) {
        EmpleCodeShortUrlAppendInfo appendInfo = JSON.parseObject(mapping.getAppendInfo(), EmpleCodeShortUrlAppendInfo.class);
        if (appendInfo == null || StringUtils.isBlank(appendInfo.getCorpId()) || appendInfo.getId() == null ) {
            throw new CustomException(ResultTip.TIP_APPEND_MISSING);
        }
        Long codeId = appendInfo.getId();
        // 查看活码详情
        WeEmpleCodeVO empleCode = weempleCodeMapper.selectWeEmpleCodeById(codeId, appendInfo.getCorpId());
        String codeUrl ;
        if (empleCode == null || StringUtils.isBlank(empleCode.getQrCode())) {
            // 不存在则给前端返回空串
            codeUrl = StringUtils.EMPTY ;
        }else {
            codeUrl = empleCode.getQrCode();
        }
        return MiniAppConst.genEmpleCodeQueryString(shortUrlType().getType(), codeUrl);
    }
}
