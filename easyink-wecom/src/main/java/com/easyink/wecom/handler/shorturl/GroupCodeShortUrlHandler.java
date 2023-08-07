package com.easyink.wecom.handler.shorturl;

import com.alibaba.fastjson.JSON;
import com.easyink.common.config.WechatOpenConfig;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import com.easyink.common.shorturl.model.GroupCodeShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.wecom.client.WechatOpenClient;
import com.easyink.wecom.domain.WeGroupCode;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.groupcode.GroupCodeActivityFirstVO;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.service.WeGroupCodeService;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.UriEncoder;


import static com.easyink.common.constant.miniapp.MiniAppConst.*;

/**
 * 类名: 群活码短链处理类
 *
 * @author : silver_chariot
 * @date : 2023/3/14 18:08
 **/
@Slf4j
@Component("groupCodeShortUrlHandler")
public class GroupCodeShortUrlHandler extends EmpleCodeShortUrlHandler<GroupCodeShortUrlAppendInfo> {

    private final WeGroupCodeService weGroupCodeService;

    public GroupCodeShortUrlHandler(WechatOpenConfig wechatOpenConfig, WechatOpenClient wechatOpenClient, WeEmpleCodeMapper weempleCodeMapper, WeGroupCodeService weGroupCodeService, WechatOpenService wechatOpenService) {
        super(wechatOpenConfig, wechatOpenClient, weempleCodeMapper, wechatOpenService);
        this.weGroupCodeService = weGroupCodeService;
    }

    @Override
    protected String handleByMapping(SysShortUrlMapping mapping) {
        return super.handleByMapping(mapping);
    }

    @Override
    protected String genQueryString(SysShortUrlMapping mapping) {
        GroupCodeShortUrlAppendInfo appendInfo = JSON.parseObject(mapping.getAppendInfo(), GroupCodeShortUrlAppendInfo.class);
        if (appendInfo == null || StringUtils.isBlank(appendInfo.getCorpId())) {
            throw new CustomException(ResultTip.TIP_APPEND_MISSING);
        }
        // 群活码id
        Long groupCodeId = appendInfo.getGroupCodeId();
        // 获取群活码详情
        WeGroupCode groupCode = weGroupCodeService.getById(groupCodeId);
        GroupCodeActivityFirstVO actualCode = null ;
        if (groupCode == null) {
            log.info("[小程序活码]找不到群活码详情,mapping:{}", mapping) ;
        }else {
            // 获取真实群活码
             actualCode = weGroupCodeService.doGetActual(groupCodeId, groupCode);
            if (actualCode == null || StringUtils.isBlank(actualCode.getActualQRCode())) {
                log.info("[小程序活码]找不到群活码实际活码,mapping:{}", mapping) ;
            }
        }
        return genGroupCodeQueryString(shortUrlType().getType(), actualCode);
    }

    @Override
    protected String getMiniAppPagePath() {
        return GROUP_CODE_PAGE_PATH;
    }

    @Override
    public ShortUrlTypeEnum shortUrlType() {
        return ShortUrlTypeEnum.GROUP_CODE;
    }

    /**
     * 生成 群活码小程序给前端的queryString
     *
     * @param type       短链类型
     * @param actualCode 真实群活码信息 {@link GroupCodeActivityFirstVO }
     * @return querystring
     */
    public String genGroupCodeQueryString(Integer type, GroupCodeActivityFirstVO actualCode) {
        if (actualCode == null) {
            return StringUtils.EMPTY;
        }
        return GROUP_CODE_QUERY_STRING.replace(TYPE_SUB, String.valueOf(type))
                                      .replace(CODE_URL, UriEncoder.encode(StringUtils.isNotBlank(actualCode.getActualQRCode()) ? actualCode.getActualQRCode() : StringUtils.EMPTY))
                                      .replace(ACT_NAME, UriEncoder.encode(StringUtils.isNotBlank(actualCode.getActivityName()) ? actualCode.getActivityName() : StringUtils.EMPTY))
                                      .replace(TIP_MSG, UriEncoder.encode(StringUtils.isNotBlank(actualCode.getTipMsg()) ? actualCode.getTipMsg() : StringUtils.EMPTY))
                                      .replace(GUIDE, UriEncoder.encode(StringUtils.isNotBlank(actualCode.getGuide()) ? actualCode.getGuide(): StringUtils.EMPTY))
                                      .replace(IS_OPEN_TIP, UriEncoder.encode(StringUtils.isNotBlank(actualCode.getIsOpenTip()) ? actualCode.getIsOpenTip() : StringUtils.EMPTY))
                                      .replace(SERVICE_QR_CODE, UriEncoder.encode(StringUtils.isNotBlank(actualCode.getServiceQrCode()) ? actualCode.getServiceQrCode() : StringUtils.EMPTY))
                                      .replace(GROUP_NAME, UriEncoder.encode(StringUtils.isNotBlank(actualCode.getGroupName()) ? actualCode.getGroupName() : StringUtils.EMPTY));
    }

    /**
     * 通过短链获取公众号配置
     *
     * @param shortCode {@link SysShortUrlMapping}
     * @return WeOpenConfig
     */
    @Override
    public WeOpenConfig getWeOpenConfig(SysShortUrlMapping shortCode) {
        return super.getWeOpenConfig(shortCode);
    }
}
