package com.easywecom.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easywecom.common.constant.Constants;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.BaseException;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.exception.wecom.WeComException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.WeCropTagClient;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.WeTagGroup;
import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.easywecom.wecom.domain.dto.tag.*;
import com.easywecom.wecom.mapper.WeTagGroupMapper;
import com.easywecom.wecom.service.WeTagGroupService;
import com.easywecom.wecom.service.WeTagService;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.easywecom.common.enums.WeExceptionTip.WE_EXCEPTION_TIP_81011;

/**
 * 标签组Service业务层处理
 *
 * @author admin
 * @date 2020-09-07
 */
@Slf4j
@Service
public class WeTagGroupServiceImpl extends ServiceImpl<WeTagGroupMapper, WeTagGroup> implements WeTagGroupService {
    @Autowired
    private WeTagGroupMapper weTagGroupMapper;

    @Autowired
    private WeCropTagClient weCropTagClient;

    @Autowired
    private WeTagService weTagService;


    /**
     * 查询标签组列表
     *
     * @param weTagGroup 标签组
     * @return 标签组
     */
    @Override
    public List<WeTagGroup> selectWeTagGroupList(WeTagGroup weTagGroup) {
        if (weTagGroup == null || org.apache.commons.lang3.StringUtils.isBlank(weTagGroup.getCorpId())) {
            log.error("查询标签组列表失败,企业id不能为空");
            throw new BaseException("查询标签组列表失败");
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(weTagGroup.getSearchName())) {
            return weTagGroupMapper.selectWetagGroupListBySearchName(weTagGroup.getSearchName(), weTagGroup.getCorpId());
        }
        List<WeTagGroup> weTagGroups = weTagGroupMapper.selectWeTagGroupList(weTagGroup);
        for (WeTagGroup tagGroup : weTagGroups) {
            //根据自增SeqId排序
            List<WeTag> list = tagGroup.getWeTags().stream().sorted(Comparator.comparing(WeTag::getSeqId)).collect(Collectors.toList());
            tagGroup.setWeTags(list);
        }
        return weTagGroups;
    }

    /**
     * 新增标签组
     *
     * @param weTagGroup 标签组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertWeTagGroup(WeTagGroup weTagGroup) {
        if (weTagGroup == null || org.apache.commons.lang3.StringUtils.isBlank(weTagGroup.getCorpId())) {
            log.error("新增标签组失败，公司id不能为空");
            throw new BaseException("新增标签组id失败");
        }
        List<WeTag> weTags = weTagGroup.getWeTags();
        if (CollUtil.isEmpty(weTags)) {
            this.save(weTagGroup);
        } else {
            //使用企微新接口
            WeCropGropTagDtlDTO weCropGropTagDtlDTO = weCropTagClient.addCorpTag(WeCropGroupTagDTO.transformAddTag(weTagGroup), weTagGroup.getCorpId());
            List<String> tagNameList = weTags.stream().map(WeTag::getName).collect(Collectors.toList());
            //用来装按传参顺序排序后的标签内容
            List<WeCropTagDTO> cropTagDTOList = new ArrayList<>();
            List<WeCropTagDTO> tags = weCropGropTagDtlDTO.getTag_group().getTag();
            //让企微新接口的标签返回值按传参顺序排序，以便后期标签排序
            for (String tagName : tagNameList) {
                for (WeCropTagDTO tag : tags) {
                    if (tagName.equals(tag.getName())) {
                        cropTagDTOList.add(tag);
                        break;
                    }
                }
            }
            weCropGropTagDtlDTO.getTag_group().setTag(cropTagDTOList);

            if (weCropGropTagDtlDTO.getErrcode().equals(WeConstans.WE_SUCCESS_CODE) && weCropGropTagDtlDTO.getTag_group() != null) {
                this.batchSaveOrUpdateTagGroupAndTag(ListUtil.toList(weCropGropTagDtlDTO.getTag_group()), false, weTagGroup.getCorpId());
            }

        }
    }

    /**
     * 修改标签组
     *
     * @param weTagGroup 标签组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWeTagGroup(WeTagGroup weTagGroup) {
        if (weTagGroup == null || org.apache.commons.lang3.StringUtils.isBlank(weTagGroup.getCorpId())) {
            log.error("修改标签组失败，企业id不能为空");
            throw new BaseException("修改标签组失败");
        }

        List<WeTag> weTags = weTagGroup.getWeTags();
        if (CollUtil.isEmpty(weTags)) {
            throw new WeComException("参数不合法");
        }
        Long num = 1000L;
        //获取新增的集合
        List<WeTag> filterWeTags = weTags.stream().filter(v -> StringUtils.isEmpty(v.getTagId())).collect(Collectors.toList());

        WeCropGroupTagDTO weCropGroupTagDTO = WeCropGroupTagDTO.transformAddTag(weTagGroup);
        //同步新增标签到微信端
        if (CollUtil.isNotEmpty(weCropGroupTagDTO.getTag())) {
            //企微新接口
            WeCropGropTagDtlDTO weCropGropTagDtlDTO = weCropTagClient.addCorpTag(weCropGroupTagDTO, weTagGroup.getCorpId());
            if (weCropGropTagDtlDTO != null && WeConstans.WE_SUCCESS_CODE.equals(weCropGropTagDtlDTO.getErrcode())) {
                //微信端返回的标签主键,设置到weTags中
                Map<String, WeCropTagDTO> weCropTagMap = weCropGropTagDtlDTO.getTag_group().getTag().stream()
                        .collect(Collectors.toMap(WeCropTagDTO::getName, weCropTagDTO -> weCropTagDTO));
                if (weCropTagMap != null) {
                    filterWeTags.forEach(tag -> {
                        tag.setTagId(weCropTagMap.get(tag.getName()).getId());
                        tag.setCreateTime(new Date(weCropTagMap.get(tag.getName()).getCreate_time() * num));
                        tag.setGroupId(weTagGroup.getGroupId());
                        tag.setCorpId(weTagGroup.getCorpId());
                    });
                }
                //保存或更新wetag
                weTagService.saveOrUpadteWeTag(filterWeTags);
            }
        }

        //获取减量标签列表
        List<String> tagIdList = weTags.stream().map(WeTag::getTagId).filter(org.apache.commons.lang3.StringUtils::isNotBlank).collect(Collectors.toList());
        LambdaQueryWrapper<WeTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeTag::getGroupId, weTagGroup.getGroupId())
                .eq(WeTag::getCorpId, weTagGroup.getCorpId())
                .eq(WeTag::getStatus, Constants.NORMAL_CODE);
        if (CollUtil.isNotEmpty(tagIdList)) {
            queryWrapper.notIn(WeTag::getTagId, tagIdList);
        }
        List<WeTag> removeWeTags = weTagService.list(queryWrapper);

        if (CollUtil.isNotEmpty(removeWeTags)) {
            //同步删除微信端的标签
            WeResultDTO weResultDTO = weCropTagClient.delCorpTag(
                    WeCropDelDTO.builder()
                            .tag_id(ArrayUtil.toArray(removeWeTags.stream().map(WeTag::getTagId).collect(Collectors.toList()), String.class))
                            .build(), weTagGroup.getCorpId());
            if (!WeConstans.WE_SUCCESS_CODE.equals(weResultDTO.getErrcode())) {
                throw new CustomException(ResultTip.TIP_DELETE_TAG_NOT_PERMISSIONS);
            }
            //移除本地
            removeWeTags.forEach(v -> v.setStatus(Constants.DELETE_CODE));
            weTagService.updatWeTagsById(removeWeTags);
        }


    }


    /**
     * 批量删除标签组
     *
     * @param ids 需要删除的标签组ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWeTagGroupByIds(String[] ids, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId) || ids == null || ids.length == 0) {
            log.error("批量删除标签组，企业id不能为空");
            throw new BaseException("批量删除标签组失败");
        }
        int returnCode = weTagGroupMapper.deleteWeTagGroupByIds(ids, corpId);

        //标签组id唯一，是内部调用，所以不需要corpId
        weTagService.deleteWeTagByGroupId(ids);
        if (returnCode > Constants.SERVICE_RETURN_SUCCESS_CODE) {
            //企微新接口
            WeResultDTO weResultDTO = weCropTagClient.delCorpTag(
                    WeCropDelDTO.builder()
                            .group_id(ids)
                            .build(), corpId
            );
            if (!WeConstans.WE_SUCCESS_CODE.equals(weResultDTO.getErrcode())) {
                if (WE_EXCEPTION_TIP_81011.getCode().equals(weResultDTO.getErrcode())) {
                    throw new CustomException(ResultTip.TIP_DELETE_TAG_NOT_PERMISSIONS);
                }
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        }

        return returnCode;
    }


    /**
     * 同步标签
     */
    @Async
    @Override
    @Transactional
    public void synchWeTags(String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            log.error("同步标签失败，企业id不能为空");
            throw new BaseException("同步标签失败");
        }
        log.info("开始同步标签,{}", corpId);
        //使用企微新接口
        WeCropGroupTagListDTO weCropGroupTagListDTO = weCropTagClient.getAllCorpTagList(corpId);

        if (weCropGroupTagListDTO.getErrcode().equals(WeConstans.WE_SUCCESS_CODE)) {
            this.batchSaveOrUpdateTagGroupAndTag(weCropGroupTagListDTO.getTag_group(), true, corpId);
        }

    }


    /**
     * 来自微信端批量保存或者更新标签组和标签
     *
     * @param tagGroupList
     * @param isSync       是否同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveOrUpdateTagGroupAndTag(List<WeCropGroupTagDTO> tagGroupList, Boolean isSync, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            log.error("批量保存或者更新标签组和标签失败，客户id不能为空");
            throw new BaseException("批量保存或者更新标签组和标签失败");
        }
        Long num = 1000L;
        List<WeTagGroup> weTagGroups = new ArrayList<>();
        List<WeTag> weTagList = new ArrayList<>();
        //数据转换:把所有标签组整成一个集合，把所有标签整成一个集合
        if (CollUtil.isNotEmpty(tagGroupList)) {
            tagGroupList.forEach(tagGroup -> {
                WeTagGroup weTagGroup = new WeTagGroup();
                weTagGroup.setCreateBy("");
                weTagGroup.setGroupName(tagGroup.getGroup_name());
                weTagGroup.setCorpId(corpId);
                weTagGroup.setGroupId(tagGroup.getGroup_id());
                weTagGroup.setCreateTime(new Date(tagGroup.getCreate_time() * num));
                List<WeCropTagDTO> tagList = tagGroup.getTag();
                if (CollUtil.isNotEmpty(tagList)) {
                    tagList.forEach(tag -> {
                        WeTag weTag = new WeTag();
                        weTag.setTagId(tag.getId());
                        weTag.setCorpId(corpId);
                        weTag.setGroupId(weTagGroup.getGroupId());
                        weTag.setName(tag.getName());
                        weTag.setCreateTime(new Date(tag.getCreate_time() * num));
                        weTagList.add(weTag);
                    });
                }
                weTagGroups.add(weTagGroup);
            });
        }
        //如果是同步标签时拉取所有标签组是空的话：逻辑删除本地所有标签组
        if (CollUtil.isEmpty(weTagGroups) && isSync) {
            List<WeTagGroup> weTagGroupList
                    = this.list(new LambdaQueryWrapper<WeTagGroup>()
                    .eq(WeTagGroup::getCorpId, corpId)
                    .eq(WeTagGroup::getStatus, Constants.NORMAL_CODE));
            if (CollUtil.isNotEmpty(weTagGroupList)) {
                weTagGroupList.forEach(k -> k.setStatus(Constants.DELETE_CODE));
                this.updateBatchById(weTagGroupList);
            }
        }
        //如果是同步标签时拉取所有标签是空的话：逻辑删除本地所有标签
        if (CollUtil.isEmpty(weTagList) && isSync) {
            List<WeTag> weTags
                    = weTagService.list(new LambdaQueryWrapper<WeTag>()
                    .eq(WeTag::getCorpId, corpId)
                    .eq(WeTag::getStatus, Constants.NORMAL_CODE));
            if (CollUtil.isNotEmpty(weTags)) {
                weTags.forEach(k -> k.setStatus(Constants.DELETE_CODE));
                weTagService.updateBatchById(weTags);
            }
        }

        //同步标签组时删除本地与远端的减量差异
        if (CollUtil.isNotEmpty(weTagGroups) && isSync) {
            List<WeTagGroup> noExist
                    = this.list(new LambdaQueryWrapper<WeTagGroup>()
                    .eq(WeTagGroup::getCorpId, corpId)
                    .notIn(WeTagGroup::getGroupId, weTagGroups.stream().map(WeTagGroup::getGroupId).collect(Collectors.toList()))
                    .eq(WeTagGroup::getStatus, Constants.NORMAL_CODE));
            if (CollUtil.isNotEmpty(noExist)) {
                noExist.forEach(k -> k.setStatus(Constants.DELETE_CODE));
                this.updateBatchById(noExist);
            }
        }
        //保存更新远端标签组数据
        if (CollUtil.isNotEmpty(weTagGroups)) {
            this.saveOrUpdateBatch(weTagGroups);
        }

        //同步标签时删除本地与远端的减量差异
        if (CollUtil.isNotEmpty(weTagList) && isSync) {
            List<WeTag> noExistWeTags
                    = weTagService.list(new LambdaQueryWrapper<WeTag>()
                    .eq(WeTag::getCorpId, corpId)
                    .notIn(WeTag::getTagId, weTagList.stream().map(WeTag::getTagId).collect(Collectors.toList()))
                    .eq(WeTag::getStatus, Constants.NORMAL_CODE)
            );
            if (CollUtil.isNotEmpty(noExistWeTags)) {
                noExistWeTags.forEach(noExistTag -> noExistTag.setStatus(Constants.DELETE_CODE));
                weTagService.updateBatchById(noExistWeTags);
            }
        }
        //保存更新远端标签数据
        if (CollUtil.isNotEmpty(weTagList)) {
            weTagService.saveOrUpdateBatch(weTagList);
        }
    }

    @Override
    public void createTagGroup(String id, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(id, corpId)) {
            log.error("创建标签失败，tagId：{}，corpId：{}", id, corpId);
            throw new BaseException("创建标签失败");
        }
        Long num = 1000L;
        List<String> list = new ArrayList<>();
        list.add(id);
        WeFindCropTagParam build = WeFindCropTagParam.builder().group_id(list).build();

        //企微新接口
        WeCropGroupTagListDTO weCropGroupTagListDTO = weCropTagClient.getCorpTagListByTagIds(build, corpId);

        List<WeCropGroupTagDTO> tagGroups = weCropGroupTagListDTO.getTag_group();
        if (CollUtil.isNotEmpty(tagGroups)) {
            List<WeTagGroup> tagGroupsList = new ArrayList<>();
            tagGroups.forEach(k -> {
                WeTagGroup tagGroupInfo = this.baseMapper.selectOne(new LambdaQueryWrapper<WeTagGroup>()
                        .eq(WeTagGroup::getGroupId, k.getGroup_id())
                        .eq(WeTagGroup::getCorpId, corpId)
                        .eq(WeTagGroup::getStatus, Constants.NORMAL_CODE));
                if (tagGroupInfo == null) {
                    WeTagGroup weTagGroup = new WeTagGroup();
                    weTagGroup.setCreateTime(new Date(k.getCreate_time() * num));
                    weTagGroup.setGroupName(k.getGroup_name());
                    weTagGroup.setCorpId(corpId);
                    weTagGroup.setGroupId(k.getGroup_id());
                    tagGroupsList.add(weTagGroup);
                }
            });
            if (CollUtil.isNotEmpty(tagGroupsList)) {
                this.saveBatch(tagGroupsList);
            }
        }
    }

    @Override
    public void deleteTagGroup(String id, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(corpId, id)) {
            log.error("删除标签组失败，corpId：{}，id：{}", corpId, id);
            throw new BaseException("删除标签组失败");
        }
        this.baseMapper.deleteWeTagGroupByIds(id.split(","), corpId);
        weTagService.deleteWeTagByGroupId(id.split(","));
    }

    @Override
    public void updateTagGroup(String id, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(id, corpId)) {
            log.error("根据标签id修改标签失败，tagId：{}，corpId：{}", id, corpId);
            throw new BaseException("根据标签id修改标签失败");
        }

        List<String> list = new ArrayList<>();
        list.add(id);
        WeFindCropTagParam build = WeFindCropTagParam.builder().group_id(list).build();

        //企微新接口
        WeCropGroupTagListDTO weCropGroupTagListDTO = weCropTagClient.getCorpTagListByTagIds(build, corpId);
        List<WeCropGroupTagDTO> tagGroups = weCropGroupTagListDTO.getTag_group();
        if (CollUtil.isNotEmpty(tagGroups)) {
            List<WeTagGroup> tagGroupsList = new ArrayList<>();
            WeTagGroup weTagGroup = new WeTagGroup();
            tagGroups.forEach(k -> {
                //和产品核实，以后不需要使用createBy
                weTagGroup.setGroupName(k.getGroup_name());
                weTagGroup.setGroupId(k.getGroup_id());
                weTagGroup.setCorpId(corpId);
                weTagGroup.setCreateTime(new Date(k.getCreate_time() * 1000L));
                tagGroupsList.add(weTagGroup);
            });
            if (CollUtil.isNotEmpty(tagGroupsList)) {
                this.saveOrUpdateBatch(tagGroupsList);
            }
        }
    }

    @Override
    public List<WeTagGroup> findCustomerTagByFlowerCustomerRelId(String flowerCustomerRelId) {
        return this.baseMapper.findCustomerTagByFlowerCustomerRelId(flowerCustomerRelId);
    }


}
