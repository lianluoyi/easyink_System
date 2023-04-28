package com.easyink.wecom.service.impl.radar;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.constant.radar.RadarConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.AttachmentTypeEnum;
import com.easyink.common.enums.MessageType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.radar.*;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import com.easyink.common.shorturl.model.RadarShortUrlAppendInfo;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.sql.SqlUtil;
import com.easyink.wecom.annotation.Convert2Cipher;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.dto.radar.DeleteRadarDTO;
import com.easyink.wecom.domain.dto.radar.RadarDTO;
import com.easyink.wecom.domain.dto.radar.SearchRadarDTO;
import com.easyink.wecom.domain.entity.radar.WeRadar;
import com.easyink.wecom.domain.entity.radar.WeRadarChannel;
import com.easyink.wecom.domain.entity.radar.WeRadarTag;
import com.easyink.wecom.domain.entity.radar.WeRadarUrl;
import com.easyink.wecom.domain.vo.radar.WeRadarVO;
import com.easyink.wecom.handler.shorturl.RadarShortUrlHandler;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.radar.WeRadarMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.radar.WeRadarChannelService;
import com.easyink.wecom.service.radar.WeRadarClickRecordService;
import com.easyink.wecom.service.radar.WeRadarService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClassName： WeRadarServiceImpl
 *
 * @author wx
 * @date 2022/7/18 15:35
 */
@Slf4j
@Service
public class WeRadarServiceImpl extends ServiceImpl<WeRadarMapper, WeRadar> implements WeRadarService {

    /**
     * 行为通知模板
     */
    private final static String RADAR_BEHAVIOR_NOTICE = "${customer}打开了您发布的${radarTitle}雷达链接";
    private final WeRadarChannelService radarChannelService;
    private final WeCorpAccountService corpAccountService;
    private final WeMessagePushClient messagePushClient;
    private final WeUserService weUserService;
    private final WeMessagePushClient weMessagePushClient;
    private final WeRadarClickRecordService weRadarClickRecordService;
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;
    private final WeCustomerService weCustomerService;

    private final RadarShortUrlHandler radarUrlHandler;

    private final WeFlowerCustomerRelService weFlowerCustomerRelService;


    @Autowired
    @Lazy
    public WeRadarServiceImpl(WeRadarChannelService radarChannelService, WeCorpAccountService corpAccountService, WeMessagePushClient messagePushClient, WeUserService weUserService, WeMessagePushClient weMessagePushClient, WeRadarClickRecordService weRadarClickRecordService, WeCorpAccountService weCorpAccountService, WeCustomerTrajectoryService weCustomerTrajectoryService, WeCustomerService weCustomerService, RadarShortUrlHandler radarUrlHandler, WeFlowerCustomerRelService weFlowerCustomerRelService) {
        this.radarChannelService = radarChannelService;
        this.corpAccountService = corpAccountService;
        this.messagePushClient = messagePushClient;
        this.weUserService = weUserService;
        this.weMessagePushClient = weMessagePushClient;
        this.weRadarClickRecordService = weRadarClickRecordService;
        this.weCorpAccountService = weCorpAccountService;
        this.weCustomerTrajectoryService = weCustomerTrajectoryService;
        this.weCustomerService = weCustomerService;
        this.radarUrlHandler = radarUrlHandler;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
    }

    /**
     * 新增雷达
     *
     * @param radarDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRadar(RadarDTO radarDTO) {
        verifyParam(radarDTO);
        WeRadar weRadar = radarDTO.buildRadarData();
        List<WeRadarTag> weRadarTagList = radarDTO.buildRadarTags(weRadar.getId());
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            weRadar.setCreateBy(LoginTokenService.getUsername());
        } else {
            radarDTO.setMainDepartment(LoginTokenService.getLoginUser().getWeUser().getMainDepartment());
            weRadar.setCreateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        weRadar.setCreateTime(DateUtils.getTime());
        if (CollectionUtils.isNotEmpty(weRadarTagList)) {
            this.baseMapper.saveRadarTags(weRadarTagList);
        }
        this.save(weRadar);
        //发消息通知员工
        if (Boolean.TRUE.equals(radarDTO.getEnableUpdateNotice())) {
            sendToUser(weRadar.getCorpId(), weRadar.getRadarTitle(), weRadar.getType());
        }
    }

    /**
     * 给员工发送应用消息
     *
     * @param corpId
     * @param radarTitle
     * @param radarType
     */
    private void sendToUser(String corpId, String radarTitle, Integer radarType) {
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(corpId);
        String agentId = validWeCorpAccount.getAgentId();
        // 文本消息
        TextMessageDTO text = new TextMessageDTO();
        StringBuilder content = new StringBuilder();
        //设置发送者 发送给企业所有员工
        if (RadarTypeEnum.CORP.getType().equals(radarType)) {
            pushDto.setTouser(RadarConstants.UpdateNoticeToUser.SEND_ALL);
            content.append(RadarConstants.UpdateNoticeToUser.CORP_RADAR_UPDATE);
            if (StringUtils.isNotBlank(radarTitle)) {
                content.append(RadarConstants.UpdateNoticeToUser.getUpdateMessage(radarTitle));
            }
            text.setContent(content.toString());
        } else if (RadarTypeEnum.DEPARTMENT.getType().equals(radarType)) {
            //设置发送者 发送给部门所有员工
            pushDto.setToparty(String.valueOf(LoginTokenService.getLoginUser().getWeUser().getMainDepartment()));
            content.append(RadarConstants.UpdateNoticeToUser.DEPARTMENT_RADAR_UPDATE);
            if (StringUtils.isNotBlank(radarTitle)) {
                content.append(RadarConstants.UpdateNoticeToUser.getUpdateMessage(radarTitle));
            }
            text.setContent(content.toString());
        } else {
            //没命中说明是个人雷达不发消息
            return;
        }
        pushDto.setAgentid(Integer.valueOf(agentId));
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        log.debug("发送雷达变更信息：toUser:{},toParty:{}", pushDto.getTouser(), pushDto.getToparty());
        messagePushClient.sendMessageToUser(pushDto, agentId, corpId);
    }


    /**
     * 查询雷达列表
     *
     * @param radarDTO
     * @return
     */
    @Override
    public List<WeRadarVO> getRadarList(SearchRadarDTO radarDTO) {
        final boolean isSuperAdmin = LoginTokenService.getLoginUser().isSuperAdmin();
        if (isSuperAdmin) {
            if (RadarTypeEnum.DEPARTMENT.getType().equals(radarDTO.getType())) {
                return Collections.emptyList();
            }
        }
        if (!isSuperAdmin) {
            if (RadarTypeEnum.DEPARTMENT.getType().equals(radarDTO.getType())) {
                final List<String> userIds = weUserService.listOfUserId(radarDTO.getCorpId(), StringUtils.join(LoginTokenService.getLoginUser().getWeUser().getMainDepartment(), StrUtil.COMMA));
                radarDTO.setUserIds(userIds);
            } else if (RadarTypeEnum.SELF.getType().equals(radarDTO.getType())) {
                List<String> userIds = new ArrayList<>();
                userIds.add(LoginTokenService.getLoginUser().getWeUser().getUserId());
                radarDTO.setUserIds(userIds);
            }
        }
        startPage();
        return this.baseMapper.list(radarDTO);
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (com.easyink.common.utils.StringUtils.isNotNull(pageNum) && com.easyink.common.utils.StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 获取详情
     *
     * @param corpId
     * @param id
     * @return
     */
    @Override
    public WeRadarVO getRadar(String corpId, Long id) {
        WeRadarVO radarVO = this.baseMapper.getOne(corpId, id);
        if (ObjectUtils.isEmpty(radarVO)) {
            return new WeRadarVO();
        }
        if (Constants.SUPER_ADMIN.equals(radarVO.getCreateId())) {
            radarVO.setCreateName(Constants.SUPER_ADMIN);
        }
        return radarVO;
    }

    /**
     * 更新雷达
     *
     * @param radarDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRadar(RadarDTO radarDTO) {
        verifyParam(radarDTO);
        WeRadar weRadar = radarDTO.buildRadarData();
        weRadar.setId(radarDTO.getId());
        List<WeRadarTag> weRadarTagList = radarDTO.buildRadarTags(weRadar.getId());
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            weRadar.setUpdateBy(LoginTokenService.getUsername());
        } else {
            weRadar.setUpdateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        weRadar.setUpdateTime(DateUtils.getTime());
        this.baseMapper.deleteRadarTags(radarDTO.getId());
        if (CollectionUtils.isNotEmpty(weRadarTagList)) {
            this.baseMapper.saveRadarTags(weRadarTagList);
        }
        this.baseMapper.updateById(weRadar);
        //发消息通知员工
        if (Boolean.TRUE.equals(radarDTO.getEnableUpdateNotice())) {
            sendToUser(radarDTO.getCorpId(), radarDTO.getRadarTitle(), radarDTO.getType());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchRemoveRadar(String corpId, DeleteRadarDTO deleteDTO) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        this.baseMapper.batchDeleteRadarTags(deleteDTO.getIdList());
        radarChannelService.remove(new LambdaQueryWrapper<WeRadarChannel>()
                .in(WeRadarChannel::getRadarId, deleteDTO.getIdList()));
        return this.baseMapper.deleteBatchIds(deleteDTO.getIdList());
    }

    @Override
    public List<String> getTagListByRadarId(Long id) {
        if (id == null) {
            return Collections.emptyList();
        }
        return this.baseMapper.getTagListByRadarId(id);
    }

    @Override
    @Convert2Cipher
    public AttachmentParam getRadarShortUrl(Long radarId, Integer channelType, String userId, String corpId, String scenario) {
        if (com.easyink.common.utils.StringUtils.isBlank(corpId) && !ObjectUtils.allNotNull(radarId, channelType)) {
            log.info("【发送雷达短链】生成雷达短链参数错误，radarId:{},channelType:{}, userId:{}, corpId:{},scenario:{}", radarId, channelType, userId, corpId, scenario);
            return null;
//            throw new CustomException(RADAR_SHORT_PARAM_ERROR);
        }
        final WeRadarUrl weRadarUrl = this.baseMapper.getOne(corpId, radarId).getWeRadarUrl();
        if (ObjectUtils.isEmpty(weRadarUrl)) {
            log.info("【发送雷达短链】雷达实体查询不到，radarId:{},channelType:{}, userId:{}, corpId:{},scenario:{}", radarId, channelType, userId, corpId, scenario);
//            throw new CustomException(RADAR_SHORT_ERROR);
            return null;
        }
        AttachmentParam.AttachmentParamBuilder builder = AttachmentParam.builder();
        final AttachmentParam build = builder.content(weRadarUrl.getTitle())
                .picUrl(weRadarUrl.getCoverUrl())
                .description(weRadarUrl.getContent())
                .url(radarChannelService.createShortUrl(corpId, radarId, userId, channelType, scenario)).typeEnum(AttachmentTypeEnum.LINK).build();
        log.info("【发送雷达短链】成功生成，radarId:{},channel:{}, userId:{}, corpId:{},scenario:{}", radarId, RadarChannelEnum.getChannelByType(channelType), userId, corpId, scenario);
        return build;
    }

    /**
     * 校验参数
     *
     * @param radarDTO
     */
    private void verifyParam(RadarDTO radarDTO) {
        if (StringUtils.isEmpty(radarDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (Boolean.TRUE.equals(radarDTO.getEnableCustomerTag()) && CollectionUtils.isEmpty(radarDTO.getRadarTagList())) {
            throw new CustomException(RadarConstants.PromptCus.NOT_USE_TAG);
        }
    }


    /**
     * 客户点击表单 记录客户点击操作
     *
     * @param shortCode 短链code
     * @param openId    公众号openid
     * @return 原链接
     */
    public String recordRadar(String shortCode, String openId) {
        if (StringUtils.isBlank(shortCode)) {
            log.info("[获取雷达原链接] 短链code为空,shortCode:{},openId:{}", shortCode, openId);
            throw new CustomException(ResultTip.TIP_NEED_SHORT_CODE);
        }
        // 1. 获取长短链映射
        SysShortUrlMapping mapping = radarUrlHandler.getUrlByMapping(shortCode);
        if (StringUtils.isBlank(mapping.getLongUrl())) {
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_LONG_URL);
        }
        if (ShortUrlTypeEnum.RADAR.getType().equals(mapping.getType())) {
            // 2. 异步处理 (记录点击详情)
            try {
                asyncRadarHandle(mapping, openId);
            } catch (CustomException e) {
                log.error("[雷达异步处理]出现业务异常.code:{},openId:{},errmsg:{}", shortCode, openId, e.getMessage());
            } catch (Exception e) {
                log.error("[雷达异步处理]出现未知异常.code:{},openId:{},errmsg:{}", shortCode, openId, ExceptionUtils.getStackTrace(e));
            }
        }
        return radarUrlHandler.handleAndGetRedirectUrl(mapping);
    }

    @Async
    public void asyncRadarHandle(SysShortUrlMapping mapping, String openId) {
        if (mapping == null || StringUtils.isBlank(openId)
                || mapping.getRadarAppendInfo() == null || mapping.getRadarAppendInfo().getRadarId() == null || mapping.getRadarAppendInfo().getChannelType() == null || StringUtils.isBlank(mapping.getRadarAppendInfo().getUserId())) {
            log.error("[异步雷达触发记录处理]参数确实,无法记录点击记录,mapping:{},openid:{}", mapping, openId);
            return;
        }
        RadarShortUrlAppendInfo appendInfo = mapping.getRadarAppendInfo();
        // 1. 获取雷达详情
        WeRadar radar = getById(appendInfo.getRadarId());
        if (radar == null || StringUtils.isBlank(radar.getCorpId())) {
            log.error("[异步雷达触发记录处理]找不到对应的雷达详情,无法记录点击记录,mapping:{}", mapping);
            return;
        }
        //  1.根据openid获取客户详情
        WeCustomer customer = getCustomerInfoByOpenId(openId, radar.getCorpId());
        if (customer == null) {
            log.error("[异步雷达触发记录处理]获取客户详情,查询不到客户信息,openid:{}", openId);
            throw new CustomException(ResultTip.TIP_FAIL_TO_GET_CUSTOMER_INFO);
        }
        // 3. 获取员工详情
        WeUser user;
        if (UserConstants.INIT_ADMIN_ROLE_KEY.equals(appendInfo.getUserId())) {
            user = getLastUser(customer.getExternalUserid(), radar.getCorpId());
        } else {
            user = weUserService.getUserDetail(radar.getCorpId(), appendInfo.getUserId());
        }
        if (user == null) {
            log.error("[异步雷达触发记录处理]找不到使用雷达的员工详情,radar:{},mapping:{},customer:{},appendInfo:{}", radar, mapping, customer, appendInfo);
        }
        // 4.  保存雷达点击记录
        weRadarClickRecordService.createRecord(appendInfo, customer, openId, user);
        // 5. 执行高级设置 (行为通知、轨迹记录、客户标签)
        doExtraSetting(radar, user, customer, appendInfo);
    }

    /**
     * 查找客户最近添加的员工
     *
     * @param externalUserid 客户id
     * @param corpId 企业id
     * @return 员工信息
     */
    private WeUser getLastUser(String externalUserid, String corpId) {
        if (StringUtils.isAnyBlank(externalUserid, corpId)) {
            log.info("[异步雷达触发记录处理] 参数缺失, externalUserid:{}, corpId:{}", externalUserid, corpId);
            return null;
        }
        // 查出该客户最近添加的员工
        WeFlowerCustomerRel flowerCustomerRel = weFlowerCustomerRelService.getLastUser(externalUserid, corpId);
        if (flowerCustomerRel == null) {
            log.info("[异步雷达触发记录处理] 用户不存在添加的员工, flowerCustomerRel:{}", flowerCustomerRel);
            throw new CustomException(ResultTip.TIP_NOT_HAVE_FOLLOW_USER);
        }
        return weUserService.getUserDetail(corpId, flowerCustomerRel.getUserId());
    }

    /**
     * 执行高级操作
     *
     * @param radar      雷达 {@link WeRadar}
     * @param user       员工信息 {@link WeUser}
     * @param customer   客户信息 {@link WeCustomer }
     * @param appendInfo 附件信息{@link RadarShortUrlAppendInfo }
     */
    private void doExtraSetting(WeRadar radar, WeUser user, WeCustomer customer, RadarShortUrlAppendInfo appendInfo) {
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
        List<String> tagIdList = getTagListByRadarId(id);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(tagIdList)) {
            log.info("[雷达高级设置处理]没有需要打上的标签,radar:{},customer:{}", id, customer);
            return;
        }
        weCustomerService.singleMarkLabel(customer.getCorpId(), user.getUserId(), customer.getExternalUserid(), tagIdList, user.getName());

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
     * @param corpId 企业id
     * @return 客户详情 {@link WeCustomer}
     */
    public WeCustomer getCustomerInfoByOpenId(String openId, String corpId) {
        if (StringUtils.isAnyBlank(openId, corpId)) {
            return null;
        }
        return weCustomerService.getCustomerInfoByOpenId(openId, corpId);
    }


}
