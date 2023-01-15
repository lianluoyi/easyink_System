package com.easyink.wecom.service.impl.radar;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.radar.RadarConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.AttachmentTypeEnum;
import com.easyink.common.enums.MessageType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.radar.*;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.sql.SqlUtil;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.WeTag;
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
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.radar.WeRadarMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeUserService;
import com.easyink.wecom.service.radar.WeRadarChannelService;
import com.easyink.wecom.service.radar.WeRadarService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    private final WeRadarChannelService radarChannelService;
    private final WeCorpAccountService corpAccountService;
    private final WeMessagePushClient messagePushClient;
    private final WeUserService weUserService;

    @Autowired
    @Lazy
    public WeRadarServiceImpl(WeRadarChannelService radarChannelService, WeCorpAccountService corpAccountService, WeMessagePushClient messagePushClient, WeUserService weUserService) {
        this.radarChannelService = radarChannelService;
        this.corpAccountService = corpAccountService;
        this.messagePushClient = messagePushClient;
        this.weUserService = weUserService;
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
    public List<WeTag> getTagListByRadarId(Long id) {
        if (id == null) {
            return Collections.emptyList();
        }
        return this.baseMapper.getTagListByRadarId(id);
    }

    @Override
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

}
