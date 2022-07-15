package com.easywecom.wecom.service.impl.redeemcode;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.annotation.DataScope;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.dto.redeemcode.WeRedeemCodeActivityDTO;
import com.easywecom.wecom.domain.dto.redeemcode.WeRedeemCodeActivityDeleteDTO;
import com.easywecom.wecom.domain.entity.redeemcode.WeRedeemCode;
import com.easywecom.wecom.domain.entity.redeemcode.WeRedeemCodeActivity;
import com.easywecom.wecom.domain.vo.redeemcode.RedeemCodeAlarmUserVO;
import com.easywecom.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.redeemcode.WeRedeemCodeActivityMapper;
import com.easywecom.wecom.service.redeemcode.WeRedeemCodeActivityService;
import com.easywecom.wecom.service.redeemcode.WeRedeemCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 兑换码活动Service业务层处理
 *
 * @author wx
 * @date 2022-07-04
 */

@Slf4j
@Service
public class WeRedeemCodeActivityServiceImpl extends ServiceImpl<WeRedeemCodeActivityMapper, WeRedeemCodeActivity> implements WeRedeemCodeActivityService {


    @Autowired
    private WeRedeemCodeService weRedeemCodeService;

    /**
     * 新增兑换码活动
     *
     * @param weRedeemCodeActivity
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveReemCodeActivity(WeRedeemCodeActivityDTO weRedeemCodeActivity) {
        if (StringUtils.isEmpty(weRedeemCodeActivity.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        //校验参数
        verifyParam(weRedeemCodeActivity);
        weRedeemCodeActivity.setCreateTime(new Date());
        LoginUser user = LoginTokenService.getLoginUser();
        if (user.isSuperAdmin()) {
            weRedeemCodeActivity.setCreateBy("admin");
        } else if (user.getWeUser() != null && StringUtils.isNotBlank(user.getWeUser().getUserId())) {
            weRedeemCodeActivity.setCreateBy(user.getWeUser().getUserId());
        }
        if (StringUtils.isBlank(weRedeemCodeActivity.getEffectStartTime())) {
            weRedeemCodeActivity.setEffectStartTime(WeConstans.REDEEM_CODE_EMPTY_TIME);
        }
        if (StringUtils.isBlank(weRedeemCodeActivity.getEffectEndTime())) {
            weRedeemCodeActivity.setEffectEndTime(WeConstans.REDEEM_CODE_EMPTY_TIME);
        }
        //保存兑换码活动数据 如果设置警告同时保存员工数据
        this.baseMapper.insert(weRedeemCodeActivity);
        //存员工关系表
        if (WeConstans.REDEEM_CODE_USER_ALARM.equals(weRedeemCodeActivity.getEnableAlarm())) {
            weRedeemCodeActivity.setAlarmUserActivityId(weRedeemCodeActivity.getId());
            this.baseMapper.insertAlarmUser(weRedeemCodeActivity.getUseUsers());
        }
        return weRedeemCodeActivity.getId();
    }

    /**
     * 分页查询兑换码活动列表
     *
     * @param weRedeemCodeActivity
     * @return
     */
    @Override
    @DataScope
    public List<WeRedeemCodeActivityVO> getReemCodeActivityList(WeRedeemCodeActivityDTO weRedeemCodeActivity) {
        if (ObjectUtil.isNull(weRedeemCodeActivity) || StringUtils.isEmpty(weRedeemCodeActivity.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (StringUtils.isNotBlank(weRedeemCodeActivity.getEffectStartTime())) {
            weRedeemCodeActivity.setEffectStartTime(DateUtils.parseBeginDay(weRedeemCodeActivity.getEffectStartTime()));
        }
        if (StringUtils.isNotBlank(weRedeemCodeActivity.getEffectEndTime())) {
            weRedeemCodeActivity.setEffectEndTime(DateUtils.parseEndDay(weRedeemCodeActivity.getEffectEndTime()));
        }
        final boolean isSuperAdmin = LoginTokenService.getLoginUser().isSuperAdmin();
        List<WeRedeemCodeActivityVO> weRedeemCodeActivityVOS = this.baseMapper.selectWeRedeemCodeActivityList(weRedeemCodeActivity, isSuperAdmin);
        weRedeemCodeActivity.setNowTime(DateUtils.getDate());
        weRedeemCodeActivityVOS.forEach(item -> {
            if (WeConstans.REDEEM_CODE_EMPTY_TIME.equals(item.getEffectStartTime())) {
                item.setEffectStartTime(StringUtils.EMPTY);
            }
            if (WeConstans.REDEEM_CODE_EMPTY_TIME.equals(item.getEffectEndTime())) {
                item.setEffectEndTime(StringUtils.EMPTY);
            }
        });
        return weRedeemCodeActivityVOS;
    }

    /**
     * 编辑更新兑换码活动
     *
     * @param weRedeemCodeActivity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWeRedeemCodeActivity(WeRedeemCodeActivityDTO weRedeemCodeActivity) {
        if (StringUtils.isEmpty(weRedeemCodeActivity.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        //校验参数
        verifyParam(weRedeemCodeActivity);
        if (StringUtils.isBlank(weRedeemCodeActivity.getEffectStartTime())) {
            weRedeemCodeActivity.setEffectStartTime(WeConstans.REDEEM_CODE_EMPTY_TIME);
        }
        if (StringUtils.isBlank(weRedeemCodeActivity.getEffectEndTime())) {
            weRedeemCodeActivity.setEffectEndTime(WeConstans.REDEEM_CODE_EMPTY_TIME);
        }
        weRedeemCodeActivity.setUpdateTime(new Date());
        weRedeemCodeActivity.setUpdateBy(LoginTokenService.getUsername());
        //保存兑换码活动数据 如果设置警告同时保存员工数据
        this.baseMapper.updateById(weRedeemCodeActivity);
        //存员工关系表
        if (weRedeemCodeActivity.getEnableAlarm().equals(WeConstans.REDEEM_CODE_USER_ALARM)) {
            weRedeemCodeActivity.setAlarmUserActivityId(weRedeemCodeActivity.getId());
            //更新员工关系表
            this.baseMapper.deleteAlarmUser(new Long[]{weRedeemCodeActivity.getId()});
            this.baseMapper.insertAlarmUser(weRedeemCodeActivity.getUseUsers());
        }
    }

    /**
     * 批量逻辑删除兑换码活动
     *
     * @param corpId
     * @param deleteDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchRemoveRedeemCodeActivity(String corpId, WeRedeemCodeActivityDeleteDTO deleteDTO) {

        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(deleteDTO.getIdList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        final Long[] ids = deleteDTO.getIdList().toArray(new Long[]{});
        //删除警告员工
        if (ArrayUtils.isEmpty(ids)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        this.baseMapper.deleteAlarmUser(ids);
        //删除兑换码库存
        weRedeemCodeService.remove(new LambdaQueryWrapper<WeRedeemCode>().in(WeRedeemCode::getActivityId, ids));
        //删除兑换码活动
        return this.baseMapper.deleteBatchByIds(corpId, ids);
    }

    /**
     * 获取兑换码活动详情
     *
     * @param corpId
     * @param id     兑换码活动id
     * @return
     */
    @Override
    public WeRedeemCodeActivityVO getRedeemCodeActivity(String corpId, Long id) {
        if (StringUtils.isBlank(corpId) || ObjectUtil.isNull(id)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        final WeRedeemCodeActivity weRedeemCodeActivity = this.baseMapper.selectOne(new LambdaQueryWrapper<WeRedeemCodeActivity>()
                .eq(WeRedeemCodeActivity::getCorpId, corpId)
                .eq(WeRedeemCodeActivity::getId, id));
        if (ObjectUtil.isNull(weRedeemCodeActivity)) {
            return new WeRedeemCodeActivityVO();
        }
        WeRedeemCodeActivityVO weRedeemCodeActivityVO = new WeRedeemCodeActivityVO();
        BeanUtils.copyProperties(weRedeemCodeActivity, weRedeemCodeActivityVO);
        List<RedeemCodeAlarmUserVO> alarmUserList = this.baseMapper.getAlarmUserList(corpId, id);
        final Integer remainInventory = this.baseMapper.getRemainInventory(String.valueOf(id));
        weRedeemCodeActivityVO.setRemainInventory(remainInventory);
        if (CollectionUtils.isNotEmpty(alarmUserList)) {
            weRedeemCodeActivityVO.setAlarmUserList(alarmUserList);
        }
        return weRedeemCodeActivityVO;
    }

    /**
     * 校验参数 时间参数
     *
     * @param weRedeemCodeActivityDTO
     */
    private void verifyParam(WeRedeemCodeActivityDTO weRedeemCodeActivityDTO) {
        if (weRedeemCodeActivityDTO == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        if (StringUtils.isNotBlank(weRedeemCodeActivityDTO.getEffectStartTime()) && StringUtils.isNotBlank(weRedeemCodeActivityDTO.getEffectEndTime())) {
            final Date endTime = DateUtils.parseDate(weRedeemCodeActivityDTO.getEffectEndTime());
            final Date startTime = DateUtils.parseDate(weRedeemCodeActivityDTO.getEffectStartTime());
            //结束时间需要大于当前日期
            if (DateUtils.diffTime(endTime, DateUtils.parseDate(DateUtils.getDate())) < 0) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_END_TIME_GE_START_TIME);
            }
            //开始时间要小于结束时间
            if (DateUtils.diffTime(startTime, endTime) > 0) {
                throw new CustomException(ResultTip.TIP_START_AFTER_END_TIME);
            }
        }
        if (StringUtils.isBlank(weRedeemCodeActivityDTO.getEffectStartTime()) && StringUtils.isNotBlank(weRedeemCodeActivityDTO.getEffectEndTime())) {
            final Date endTime = DateUtils.parseDate(weRedeemCodeActivityDTO.getEffectEndTime());
            //结束时间需要大于当前日期
            if (DateUtils.diffTime(endTime, DateUtils.parseDate(DateUtils.getDate())) < 0) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_END_TIME_GE_START_TIME);
            }
        }

        if (weRedeemCodeActivityDTO.getEnableAlarm().equals(WeConstans.REDEEM_CODE_USER_ALARM)) {
            if (weRedeemCodeActivityDTO.getAlarmThreshold() < 0) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_EMPTY_THRESHOLD);
            }
            if (CollectionUtils.isEmpty(weRedeemCodeActivityDTO.getUseUsers())) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_EMPTY_USERS);
            }
        }
    }
}
