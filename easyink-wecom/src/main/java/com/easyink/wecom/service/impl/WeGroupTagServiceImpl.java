package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeGroupTag;
import com.easyink.wecom.domain.WeGroupTagCategory;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.enums.statistics.StatisticsEnum;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupListVO;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupStatisticChartVO;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupStatisticsVO;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagRelDetail;
import com.easyink.wecom.mapper.*;
import com.easyink.wecom.service.WeGroupTagRelService;
import com.easyink.wecom.service.WeGroupTagService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名：WeGroupTagServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
@Service
public class WeGroupTagServiceImpl extends ServiceImpl<WeGroupTagMapper, WeGroupTag> implements WeGroupTagService {

    /**
     * 导出报表名
     */
    protected static final String SHEET_NAME="群标签统计报表";
    /**
     * 标签下客户群数为零
     */
    private static final int NUM_EMPTY=0;
    /**
     * 饼图获取最大需要数据条数
     */
    private static final int MAX_SHOW=10;

    private final WeGroupTagRelService weGroupTagRelService;

    @Autowired
    private  WeGroupMapper weGroupMapper;

    @Autowired
    private WeGroupTagRelMapper weGroupTagRelMapper;

    @Autowired
    private WeGroupTagMapper weGroupTagMapper;

    @Autowired
    private WeGroupTagCategoryMapper weGroupTagCategoryMapper;

    @Autowired
    private WeUserMapper weUserMapper;

    @Autowired
    public WeGroupTagServiceImpl(WeGroupTagRelService weGroupTagRelService) {
        this.weGroupTagRelService = weGroupTagRelService;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(String corpId, Long groupTagId, List<WeGroupTag> list) {
        if (StringUtils.isBlank(corpId) || groupTagId == null || CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //校验标签是否有重名
        List<String> nameList = list.stream().map(WeGroupTag::getName).distinct().collect(Collectors.toList());
        if (list.size() != nameList.size()) {
            throw new CustomException(ResultTip.TIP_GROUP_TAG_EXIST);
        }

        list.forEach(weGroupTag -> {
            weGroupTag.setCorpId(corpId);
            weGroupTag.setGroupId(groupTagId);
        });
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delTag(String corpId, List<Long> idList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(idList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //删除与客户群的关联关系
        weGroupTagRelService.delByTagIdList(corpId, idList);
        return baseMapper.delTag(corpId, idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delByGroupId(String corpId, List<Long> groupIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(groupIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //查询标签组下的tagIdList
        List<Long> tagIdList = baseMapper.getTagIdByGroupId(corpId, groupIdList);
        if (CollectionUtils.isNotEmpty(tagIdList)) {
            //删除与客户群的关联关系
            weGroupTagRelService.delByTagIdList(corpId, tagIdList);
        }
        return baseMapper.delByGroupId(corpId, groupIdList);
    }

    @Override
    @DataScope
    public List<WeTagGroupStatisticsVO> groupTagTableView(WeTagStatisticsDTO weTagStatisticsDTO) {
        //参数检验
        if (weTagStatisticsDTO == null || com.easyink.common.utils.StringUtils.isEmpty(weTagStatisticsDTO.getCorpId()) || !checkDepartmentAndUpdateUserIds(weTagStatisticsDTO)) {
            return new ArrayList<>();
        }
        //获取所需群聊的chatId,方便在下一步查询中进行过滤
        List<WeGroup> weGroupList = weGroupMapper.selectTagCountList(weTagStatisticsDTO);
        //通过所需群聊的chatId，获取符合条件的标签
        List<WeGroupTagRelDetail> weGroupTagRelDetailList = weGroupTagRelMapper.getTagIdList(weTagStatisticsDTO.getCorpId(), weGroupList);
        //获取全部的标签
        List<WeTagGroupStatisticsVO> weTagGroupStatisticsVOS = weGroupTagMapper.getTagList(weTagStatisticsDTO);
        if (weGroupList.isEmpty() || weGroupTagRelDetailList.isEmpty() || weTagGroupStatisticsVOS.isEmpty()) {
            return new ArrayList<>();
        }
        //计算每个标签下客户群总数，过滤客户群为零的数据
        List<WeTagGroupStatisticsVO> resultList = handleTagNums(weTagGroupStatisticsVOS, weGroupTagRelDetailList);
        if (CollectionUtils.isEmpty(resultList)){
            return new ArrayList<>();
        }
        //判断是否进行排序，否则默认为标签创建时间正序
        if (StringUtils.isNotBlank(weTagStatisticsDTO.getSortType()) && StringUtils.isNotBlank(weTagStatisticsDTO.getSortName())) {
            StatisticsEnum.GroupTagSortEnum.sort(weTagStatisticsDTO, resultList);
        } else {
            resultList.sort(Comparator.comparing(WeTagGroupStatisticsVO::getCreateTime));
        }
        return resultList;
    }

    @Override
    @DataScope
    public List<WeTagGroupStatisticChartVO> groupTagChartView(WeTagStatisticsDTO weTagStatisticsDTO) {
        //参数检验
        if (weTagStatisticsDTO == null || com.easyink.common.utils.StringUtils.isEmpty(weTagStatisticsDTO.getCorpId()) || !checkDepartmentAndUpdateUserIds(weTagStatisticsDTO)) {
            return new ArrayList<>();
        }
        //获取所需群聊的chatId,方便在下一步查询中进行过滤
        List<WeGroup> weGroupList = weGroupMapper.selectTagCountList(weTagStatisticsDTO);
        //通过所需群聊的chatId，获取符合条件的标签
        List<WeGroupTagRelDetail> weGroupTagRelDetailList = weGroupTagRelMapper.getTagIdList(weTagStatisticsDTO.getCorpId(), weGroupList);
        //获取全部的标签
        List<WeTagGroupStatisticsVO> weTagGroupStatisticsVOS = weGroupTagMapper.getTagList(weTagStatisticsDTO);
        //获取全部标签组
        List<WeGroupTagCategory> weGroupTagCategoryList = weGroupTagCategoryMapper.getTagCategoryList(weTagStatisticsDTO);
        if (weGroupList.isEmpty() || weGroupTagRelDetailList.isEmpty() || weTagGroupStatisticsVOS.isEmpty() || weGroupTagCategoryList.isEmpty()) {
            return new ArrayList<>();
        }
        List<WeTagGroupStatisticChartVO> weTagStatisticsChartVOList = new ArrayList<>();
        //计算每个标签下客户群总数，过滤客户群为零的数据
        List<WeTagGroupStatisticsVO> resultList = handleTagNums(weTagGroupStatisticsVOS, weGroupTagRelDetailList);
        if (CollectionUtils.isEmpty(resultList)){
            return new ArrayList<>();
        }
        //将标签数据对应到每个标签组
        for (WeGroupTagCategory weGroupTagCategory : weGroupTagCategoryList) {
            //计算去重前的客户群总数
            int totalCnt=0;
            //记录下每个标签的数据
            List<WeTagGroupStatisticsVO> tmpList = new ArrayList<>();
            //统计标签组下所有的微信群
            HashSet totalSet=new HashSet<>();
            for (WeTagGroupStatisticsVO weTagGroupStatisticsVO : resultList) {
                if (Objects.equals(String.valueOf(weGroupTagCategory.getId()), weTagGroupStatisticsVO.getTagGroupId())) {
                    tmpList.add(weTagGroupStatisticsVO);
                    totalCnt+=weTagGroupStatisticsVO.getCustomerCnt();
                    totalSet.addAll(weTagGroupStatisticsVO.getChatList());
                }
            }
            if (CollectionUtils.isNotEmpty(totalSet)) {
                //标签下客户群数据默认按照倒序排列
                tmpList.sort(Comparator.comparing(WeTagGroupStatisticsVO::getCustomerCnt, Comparator.reverseOrder()));
                //只返回前10条数据
                if (tmpList.size()>MAX_SHOW){
                    tmpList=tmpList.subList(0,10);
                }
                //重新组合标签组下的数据
                WeTagGroupStatisticChartVO weTagStatisticsChartVO = new WeTagGroupStatisticChartVO(weGroupTagCategory, tmpList,totalCnt,totalSet.size());
                weTagStatisticsChartVOList.add(weTagStatisticsChartVO);
            }
        }
        //标签组默认按照标签组创建时间正序排列
        weTagStatisticsChartVOList.sort(Comparator.comparing(WeTagGroupStatisticChartVO::getGroupTagCreateTime));
        return weTagStatisticsChartVOList;
    }

    @Override
    @DataScope
    public AjaxResult exportGroupTags(WeTagStatisticsDTO weTagStatisticsDTO) {
        List<WeTagGroupStatisticsVO> weTagGroupStatisticsVOS = groupTagTableView(weTagStatisticsDTO);
        ExcelUtil<WeTagGroupStatisticsVO> util = new ExcelUtil<>(WeTagGroupStatisticsVO.class);
        return util.exportExcel(weTagGroupStatisticsVOS, SHEET_NAME);
    }

    @Override
    public List<WeTagGroupListVO> groupTagList(WeTagStatisticsDTO weTagStatisticsDTO) {
        //获取所有标签组的名字和id
        List<WeGroupTagCategory> weGroupTagCategoryList = weGroupTagCategoryMapper.getTagCategoryList(weTagStatisticsDTO);
        List<WeTagGroupListVO> weTagGroupListVOS=new ArrayList<>();
        //组装成前端需要的字段
        for (WeGroupTagCategory weGroupTagCategory:weGroupTagCategoryList){
            WeTagGroupListVO weTagGroupListVO=new WeTagGroupListVO();
            weTagGroupListVO.setTagGroupId(String.valueOf(weGroupTagCategory.getId()));
            weTagGroupListVO.setGroupTagName(weGroupTagCategory.getName());
            weTagGroupListVOS.add(weTagGroupListVO);
        }
        return weTagGroupListVOS;
    }

    /**
     * 计算每个标签下的客户数
     *
     * @param weTagGroupStatisticsVOS 标签详细信息列表
     * @param weGroupTagRelDetailList 标签对客户群列表
     * @return 计算后的标签列表
     */
    public static List<WeTagGroupStatisticsVO> handleTagNums(List<WeTagGroupStatisticsVO> weTagGroupStatisticsVOS, List<WeGroupTagRelDetail> weGroupTagRelDetailList){
        List<WeTagGroupStatisticsVO> resultList=new ArrayList<>();
        for (WeTagGroupStatisticsVO weTagGroupStatisticsVO : weTagGroupStatisticsVOS){
            int groupCnt=0;
            List<String> chatList=new ArrayList<>();
            for (WeGroupTagRelDetail weGroupTagRelDetail:weGroupTagRelDetailList){
                if (weTagGroupStatisticsVO.getTagId().equals(String.valueOf(weGroupTagRelDetail.getTagId()))){
                    groupCnt++;
                    chatList.add(weGroupTagRelDetail.getChatId());
                }
            }
            if (groupCnt!=NUM_EMPTY){
                weTagGroupStatisticsVO.setCustomerCnt(groupCnt);
                weTagGroupStatisticsVO.setChatList(chatList);
                resultList.add(weTagGroupStatisticsVO);
            }
        }
        return resultList;
    }

    /**
     * 是否是部门查询
     * 如果只选定部门不选择员工 但是部门下没有员工则直接返回空列表
     *
     * @param dto   {@link WeTagStatisticsDTO}
     * @return true 继续查询 false:停止查询返回空list
     */
    public boolean checkDepartmentAndUpdateUserIds(WeTagStatisticsDTO dto) {
        if (dto == null || org.apache.commons.collections4.CollectionUtils.isEmpty(dto.getDepartmentIds())) {
            return true;
        }
        // 部门idList查询
        List<String> userIdsByDepartmentIds = weUserMapper.listOfUserId(dto.getCorpId(), dto.getDepartmentIds().toArray(new String[]{}));
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(userIdsByDepartmentIds) && org.apache.commons.collections4.CollectionUtils.isEmpty(dto.getUserIds())) {
            return false;
        }
        if (dto.getUserIds() == null) {
            dto.setUserIds(userIdsByDepartmentIds);
        }else {
            dto.getUserIds().addAll(userIdsByDepartmentIds);
        }
        return true;
    }

}
