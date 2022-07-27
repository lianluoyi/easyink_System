package com.easywecom.wecom.service.radar;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.config.WechatOpenConfig;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.MessageType;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.shorturl.ShortUrlAppendInfo;
import com.easywecom.common.shorturl.SysShortUrlMapping;
import com.easywecom.common.shorturl.service.ShortUrlAdaptor;
import com.easywecom.wecom.client.WeMessagePushClient;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.dto.WeMessagePushDTO;
import com.easywecom.wecom.domain.dto.message.TextMessageDTO;
import com.easywecom.wecom.domain.entity.radar.WeRadar;
import com.easywecom.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easywecom.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeCustomerService;
import com.easywecom.wecom.service.WeCustomerTrajectoryService;
import com.easywecom.wecom.service.WeUserService;
import com.easywecom.wecom.service.wechatopen.WechatOpenService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名: 雷达链接处理类
 *
 * @author : silver_chariot
 * @date : 2022/7/20 18:22
 **/
@Component
@Slf4j
public class RadarUrlHandler extends ShortUrlAdaptor {
    /**
     * 行为通知模板
     */
    private final static String RADAR_BEHAVIOR_NOTICE = "${customer}打开了您发布的${radarTitle}雷达链接";


    private final WeCustomerService weCustomerService;
    private final WechatOpenService wechatOpenService;
    private final WeRadarClickRecordService weRadarClickRecordService;
    private final WeRadarService weRadarService;
    private final WeUserService weUserService;
    private final WeMessagePushClient weMessagePushClient;
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;

    @Lazy
    public RadarUrlHandler( WeCustomerService weCustomerService, WechatOpenService wechatOpenService, WeRadarClickRecordService weRadarClickRecordService, WeRadarService weRadarService, WeUserService weUserService, WeMessagePushClient weMessagePushClient, WeCorpAccountService weCorpAccountService, WeCustomerTrajectoryService weCustomerTrajectoryService) {
        this.weCustomerService = weCustomerService;
        this.wechatOpenService = wechatOpenService;
        this.weRadarClickRecordService = weRadarClickRecordService;
        this.weRadarService = weRadarService;
        this.weUserService = weUserService;
        this.weMessagePushClient = weMessagePushClient;
        this.weCorpAccountService = weCorpAccountService;
        this.weCustomerTrajectoryService = weCustomerTrajectoryService;
    }

    /**
     * 构建短链附加信息
     *
     * @param radarId     雷达id
     * @param userId      使用雷达用户id
     * @param channelType 渠道类型 {@link com.easywecom.common.enums.radar.RadarChannelEnum}
     * @param detail      详情(如果是员工活码,则为员工活码使用场景，
     *                    如果是新客进群则为新客进群的活码名称,
     *                    如果是SOP则为SOP名称，如果是群日历，则为日历名称，
     *                    如果是自定义渠道则为自定义渠道的渠道名)
     * @return {@link ShortUrlAppendInfo }
     */
    public ShortUrlAppendInfo buildAppendInfo(Long radarId, String userId, Integer channelType, String detail) {
        return ShortUrlAppendInfo.builder().radarId(radarId).userId(userId).channelType(channelType).detail(detail).build();
    }

    /**
     * 创建雷达链接
     *
     * @param corpId     企业id
     * @param url        长链接url
     * @param createBy   创建人
     * @param appendInfo 附件信息{@link ShortUrlAppendInfo }
     * @return 雷达短链
     */
    public String createRadarUrl(String corpId, String url, String createBy, ShortUrlAppendInfo appendInfo) {
        if (StringUtils.isAnyBlank(url, corpId)) {
            log.info("[创建雷达链接]缺失长链接或者corpId,by:{},append:{},corpId:{}", createBy, appendInfo, corpId);
            throw new CustomException(ResultTip.TIP_MISSING_LONG_URL);
        }

        // 生成短链code
        appendInfo.setCorpId(corpId);
        String code = createShortCode(url, createBy, appendInfo);
        if (StringUtils.isBlank(code)) {
            throw new CustomException(ResultTip.TIP_ERROR_CREATE_SHORT_URL);
        }
        WeOpenConfig config = wechatOpenService.getConfig(corpId);
        if (config == null || StringUtils.isBlank(config.getOfficialAccountDomain())) {
            throw new CustomException(ResultTip.TIP_WECHAT_OPEN_OFFICIAL_NO_DOMAIN);
        }
        return genShortUrl(config.getOfficialAccountDomain(), code);
    }

    /**
     * 根据短链code获取原链接 并记录客户点击操作
     *
     * @param shortCode 短链code
     * @param openId    公众号openid
     * @return 原链接
     */
    public String getOriginUrlAndRecord(String shortCode, String openId) {
        if (StringUtils.isBlank(shortCode)) {
            log.info("[获取雷达原链接] 短链code为空,shortCode:{},openId:{}", shortCode, openId);
            throw new CustomException(ResultTip.TIP_NEED_SHORT_CODE);
        }
        // 1. 获取长短链映射
        SysShortUrlMapping mapping = getLongUrlMapping(shortCode);
        if (StringUtils.isBlank(mapping.getLongUrl())) {
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_LONG_URL);
        }
        // 2. 异步处理 (记录点击详情)
        try {
            asyncRadarHandle(mapping, openId);
        } catch (CustomException e) {
            log.error("[雷达异步处理]出现业务异常.code:{},openId:{},errmsg:{}", shortCode, openId, e.getMessage());
        } catch (Exception e) {
            log.error("[雷达异步处理]出现未知异常.code:{},openId:{},errmsg:{}", shortCode, openId, ExceptionUtils.getStackTrace(e));
        }
        // 3. 返回给前端
        return mapping.getLongUrl();
    }

    @Async
    public void asyncRadarHandle(SysShortUrlMapping mapping, String openId) {
        if (mapping == null || StringUtils.isBlank(openId)
                || mapping.getAppend() == null || mapping.getAppend().getRadarId() == null || mapping.getAppend().getChannelType() == null || StringUtils.isBlank(mapping.getAppend().getUserId())) {
            log.error("[异步雷达触发记录处理]参数确实,无法记录点击记录,mapping:{},openid:{}", mapping, openId);
            return;
        }
        ShortUrlAppendInfo appendInfo = mapping.getAppend();
        //  1.根据openid获取客户详情
        WeCustomer customer = getCustomerInfoByOpenId(openId);
        if (customer == null) {
            log.error("[异步雷达触发记录处理]获取客户详情,查询不到客户信息,openid:{}", openId);
            throw new CustomException(ResultTip.TIP_FAIL_TO_GET_CUSTOMER_INFO);
        }
        // 2. 获取雷达详情
        WeRadar radar = weRadarService.getById(appendInfo.getRadarId());
        if (radar == null || StringUtils.isBlank(radar.getCorpId())) {
            log.error("[异步雷达触发记录处理]找不到对应的雷达详情,无法记录点击记录,mapping:{}, customer:{}", mapping, customer);
            return;
        }
        // 3. 获取员工详情
        WeUser user = weUserService.getUserDetail(radar.getCorpId(), appendInfo.getUserId());
        if (user == null) {
            log.error("[异步雷达触发记录处理]找不到使用雷达的员工详情,radar:{},mapping:{},customer:{},appendInfo:{}", radar, mapping, customer, appendInfo);
        }
        // 4.  保存雷达点击记录
        weRadarClickRecordService.createRecord(appendInfo, customer, openId, user);
        // 5. 执行高级设置 (行为通知、轨迹记录、客户标签)
        doExtraSetting(radar, user, customer, appendInfo);
    }

    /**
     * 执行高级操作
     *
     * @param radar      雷达 {@link WeRadar}
     * @param user       员工信息 {@link WeUser}
     * @param customer   客户信息 {@link WeCustomer }
     * @param appendInfo 附件信息{@link ShortUrlAppendInfo }
     */
    private void doExtraSetting(WeRadar radar, WeUser user, WeCustomer customer, ShortUrlAppendInfo appendInfo) {
        // 获取企业应用信息
        WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(radar.getCorpId());
        if (corpAccount == null) {
            log.error("[雷达高级设置处理]获取企业信息失败,radar:{}", radar);
            return;
        }
        // 轨迹记录
        if (radar.getEnableBehaviorRecord()) {
            weCustomerTrajectoryService.recordRadarClickOperation(radar, user, customer);
        }
        // 打上客户标签
        if (radar.getEnableCustomerTag()) {
            setTagForRadarClick(radar.getId(), user, customer);
        }
        // 行为通知
        if (radar.getEnableClickNotice()) {
            String content = genClickNoticeContent(customer.getName(), radar.getRadarTitle());
            sendNotice(content, user, corpAccount.getAgentId());
        }
    }


    /**
     * 为点击雷达的客户打上标签
     *
     * @param id       雷达id
     * @param user   员工
     * @param customer 客户 {@link WeCustomer}
     */
    private void setTagForRadarClick(Long id, WeUser user, WeCustomer customer) {
        if (id == null || user == null || StringUtils.isBlank(user.getUserId())) {
            log.info("[雷达高级设置处理]打标签,参数缺失,id:{},user:{},customer:{}", id, user, customer);
            return;
        }
        // 根据雷达id 获取其标签
        List<WeTag> tagList = weRadarService.getTagListByRadarId(id);
        if (CollectionUtils.isEmpty(tagList)) {
            log.info("[雷达高级设置处理]没有需要打上的标签,radar:{},customer:{}", id, customer);
            return;
        }
        WeMakeCustomerTagVO makeCustomerTagVO = WeMakeCustomerTagVO.builder()
                .userId(user.getUserId())
                .corpId(customer.getCorpId())
                .externalUserid(customer.getExternalUserid())
                .updateBy(user.getName())
                .addTag(tagList)
                .build();
        List<WeMakeCustomerTagVO > list = new ArrayList<>();
        list.add(makeCustomerTagVO);
        weCustomerService.makeLabelbatch(list,user.getUserId());

    }

    /**
     * 触发雷达的行为通知
     *
     * @param name       客户名称
     * @param radarTitle 雷达标题
     * @return 通知内容
     */
    private String genClickNoticeContent(String name, String radarTitle) {
        if (StringUtils.isAnyBlank(name, radarTitle)) {
            return StringUtils.EMPTY;
        }
        return RADAR_BEHAVIOR_NOTICE.replace(GenConstants.CUSTOMER, name)
                .replace(GenConstants.RADAR_TITLE, radarTitle);
    }

    /**
     * 发送企业通知
     *
     * @param content 消息
     * @param user    员工
     * @param agentId 应用id
     */
    private void sendNotice(String content, WeUser user, String agentId) {
        if (user == null || StringUtils.isAnyBlank(content, user.getUserId(), user.getCorpId(), agentId)) {
            log.info("[雷达高级设置处理]发送通知,参数缺失,content:{},user:{}", content, user);
            return;
        }
        TextMessageDTO contentInfo = TextMessageDTO.builder()
                .content(content)
                .build();
        WeMessagePushDTO request = WeMessagePushDTO.builder()
                .msgtype(MessageType.TEXT.getMessageType())
                .touser(user.getUserId())
                .text(contentInfo)
                .agentid(Integer.valueOf(agentId))
                .build();
        weMessagePushClient.sendMessageToUser(request, agentId, user.getCorpId());
    }

    /**
     * 根据openId获取客户详情
     *
     * @param openId 公众号openid
     * @return 客户详情 {@link WeCustomer}
     */
    public WeCustomer getCustomerInfoByOpenId(String openId) {
        if (StringUtils.isBlank(openId)) {
            return null;
        }
        // 1. 根据openid获取unionId
        String unionId = wechatOpenService.getUnionIdByOpenId(openId);
        //  2. 先根据union_id去客户表查询是否有数据
        WeCustomer customer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getUnionid, unionId).last(GenConstants.LIMIT_1));
        if (customer == null) {
            log.info("[获取雷达原链接] 获取客户详情,根据union_id在数据库中未匹配到客户,openId:{},unionId:{},customer:{}", openId, unionId);
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_USER_BY_UNION_ID);
        }
        return customer;
    }


}
