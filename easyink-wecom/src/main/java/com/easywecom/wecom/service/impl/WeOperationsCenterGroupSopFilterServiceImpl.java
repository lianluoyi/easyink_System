package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.WeOperationsCenterSop;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeGroupTag;
import com.easywecom.wecom.domain.WeOperationsCenterGroupSopFilterCycleEntity;
import com.easywecom.wecom.domain.WeOperationsCenterGroupSopFilterEntity;
import com.easywecom.wecom.domain.vo.sop.BaseGroupSopTagVO;
import com.easywecom.wecom.domain.vo.sop.BaseGroupSopWeUserVO;
import com.easywecom.wecom.domain.vo.sop.FindGroupSopFilterVO;
import com.easywecom.wecom.mapper.WeOperationsCenterGroupSopFilterMapper;
import com.easywecom.wecom.service.WeGroupTagService;
import com.easywecom.wecom.service.WeOperationsCenterGroupSopFilterCycleService;
import com.easywecom.wecom.service.WeOperationsCenterGroupSopFilterService;
import com.easywecom.wecom.service.WeUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class WeOperationsCenterGroupSopFilterServiceImpl extends ServiceImpl<WeOperationsCenterGroupSopFilterMapper, WeOperationsCenterGroupSopFilterEntity> implements WeOperationsCenterGroupSopFilterService {

    private final WeOperationsCenterGroupSopFilterCycleService sopFilterCycleService;
    private final WeUserService weUserService;
    private final WeGroupTagService weGroupTagService;

    @Autowired
    public WeOperationsCenterGroupSopFilterServiceImpl(WeOperationsCenterGroupSopFilterCycleService sopFilterCycleService, WeUserService weUserService, WeGroupTagService weGroupTagService) {
        this.sopFilterCycleService = sopFilterCycleService;
        this.weUserService = weUserService;
        this.weGroupTagService = weGroupTagService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delByCorpIdAndSopIdList(String corpId, List<Long> sopIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sopIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        //删除循环SOP的起止时间
        sopFilterCycleService.delByCorpIdAndSopIdList(corpId, sopIdList);
        //删除筛选条件
        LambdaQueryWrapper<WeOperationsCenterGroupSopFilterEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterGroupSopFilterEntity::getCorpId, corpId);
        wrapper.in(WeOperationsCenterGroupSopFilterEntity::getSopId, sopIdList);
        baseMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroupSopFilter(WeOperationsCenterGroupSopFilterEntity sopFilterEntity, Integer sopType, String cycleStart, String cycleEnd) {
        if (sopFilterEntity == null || sopType == null || StringUtils.isBlank(sopFilterEntity.getCorpId()) || sopFilterEntity.getSopId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (WeOperationsCenterSop.SopTypeEnum.CYCLE.getSopType().equals(sopType)) {
            if (StringUtils.isBlank(cycleStart) || StringUtils.isBlank(cycleEnd)) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            sopFilterCycleService.saveOrUpdateSopFilterCycle(sopFilterEntity.getCorpId(), sopFilterEntity.getSopId(), cycleStart, cycleEnd);
        }
        baseMapper.saveOrUpdateGroupSopFilter(sopFilterEntity);
    }

    @Override
    public FindGroupSopFilterVO getDataBySopId(String corpId, Long sopId, Integer sopType) {
        if (StringUtils.isBlank(corpId) || sopId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        FindGroupSopFilterVO findGroupSopFilterVO = new FindGroupSopFilterVO();
        if (WeOperationsCenterSop.SopTypeEnum.CYCLE.getSopType().equals(sopType)) {
            WeOperationsCenterGroupSopFilterCycleEntity cycleEntity = sopFilterCycleService.getDataBySopId(corpId, sopId);
            if (cycleEntity != null) {
                findGroupSopFilterVO.setCycleStart(cycleEntity.getCycleStart());
                findGroupSopFilterVO.setCycleEnd(cycleEntity.getCycleEnd());
            }
        }

        WeOperationsCenterGroupSopFilterEntity groupSopFilterEntity = baseMapper.getDataByCorpIdAndSopId(corpId, sopId);
        if (groupSopFilterEntity == null) {
            return findGroupSopFilterVO;
        }
        findGroupSopFilterVO = new FindGroupSopFilterVO(groupSopFilterEntity, findGroupSopFilterVO.getCycleStart(), findGroupSopFilterVO.getCycleEnd());
        if (WeOperationsCenterSop.SopTypeEnum.CYCLE.getSopType().equals(sopType)) {
            WeOperationsCenterGroupSopFilterCycleEntity cycleEntity = sopFilterCycleService.getDataBySopId(corpId, sopId);
            if (cycleEntity == null) {
                return findGroupSopFilterVO;
            }
        }
        //当群主userIds不为空时，查询详情
        if(StringUtils.isNotBlank(findGroupSopFilterVO.getOwner())){
            List<String> userIdList = Arrays.asList(findGroupSopFilterVO.getOwner().split(","));

            LambdaQueryWrapper<WeUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WeUser::getCorpId,corpId)
                    .in(WeUser::getUserId,userIdList);
            List<WeUser> list = weUserService.list(wrapper);
            List<BaseGroupSopWeUserVO> ownerList = new ArrayList<>();
            for (WeUser weUser:list) {
                ownerList.add(new BaseGroupSopWeUserVO(weUser.getUserId(),weUser.getName()));
            }
            findGroupSopFilterVO.setOwnerList(ownerList);
        }
        //标签tagIds不为空时，查询详情
        if(StringUtils.isNotBlank(findGroupSopFilterVO.getTagId())){
            List<String> tagIdList = Arrays.asList(findGroupSopFilterVO.getTagId().split(","));
            LambdaQueryWrapper<WeGroupTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WeGroupTag::getCorpId,corpId)
                    .in(WeGroupTag::getId,tagIdList);
            List<WeGroupTag> list = weGroupTagService.list(wrapper);
            List<BaseGroupSopTagVO> tagList = new ArrayList<>();
            for (WeGroupTag weGroupTag:list) {
                tagList.add(new BaseGroupSopTagVO(weGroupTag.getId(),weGroupTag.getName()));
            }
            findGroupSopFilterVO.setTagList(tagList);
        }
        return findGroupSopFilterVO;
    }


}