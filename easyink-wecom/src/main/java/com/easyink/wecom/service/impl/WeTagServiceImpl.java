package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.exception.BaseException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeCropTagClient;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.tag.WeCropGroupTagDTO;
import com.easyink.wecom.domain.dto.tag.WeCropGroupTagListDTO;
import com.easyink.wecom.domain.dto.tag.WeCropTagDTO;
import com.easyink.wecom.domain.dto.tag.WeFindCropTagParam;
import com.easyink.wecom.mapper.WeTagMapper;
import com.easyink.wecom.service.WeTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 企业微信标签Service业务层处理
 *
 * @author admin
 * @date 2020-09-07
 */
@Slf4j
@Service
public class WeTagServiceImpl extends ServiceImpl<WeTagMapper, WeTag> implements WeTagService {

    @Autowired
    private WeTagMapper weTagMapper;
    @Autowired
    private WeCropTagClient weCropTagClient;

    /**
     * 批量修改客户数据
     *
     * @param list
     */
    @Override
    @Transactional
    public void updatWeTagsById(List<WeTag> list) {
        this.updateBatchById(list);
    }

    /**
     * 保存或修改客户标签
     *
     * @param list
     */
    @Override
    @Transactional
    public void saveOrUpadteWeTag(List<WeTag> list) {
        this.saveOrUpdateBatch(list);
    }

    /**
     * 删除企业微信标签信息
     *
     * @param id 企业微信标签ID
     * @return 结果
     */
    @Override
    public int deleteWeTagById(String id, String corpId) {
        return weTagMapper.deleteWeTagById(id, corpId);
    }

    /**
     * 删除企业微信标签信息
     *
     * @param groupIds 企业微信标签ID
     * @return 结果
     */
    @Override
    public int deleteWeTagByGroupId(String[] groupIds) {
        return weTagMapper.deleteWeTagByGroupId(groupIds);
    }

    @Override
    public void creatTag(String tagId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            log.error("创建标签失败，企业id不能为空");
            throw new BaseException("创建标签失败");
        }
        Long num = 1000L;
        if (StringUtils.isNotEmpty(tagId)) {
            //使用企微新接口
            WeCropGroupTagListDTO weCropGroupTagListDTO = weCropTagClient.getCorpTagListByTagIds(WeFindCropTagParam.builder().tag_id(tagId.split(",")).build(), corpId);
            List<WeCropGroupTagDTO> tagGroups = weCropGroupTagListDTO.getTag_group();
            if (CollUtil.isNotEmpty(tagGroups)) {
                tagGroups.stream().forEach(k -> {
                    List<WeCropTagDTO> tag = k.getTag();
                    if (CollUtil.isNotEmpty(tag)) {
                        List<WeTag> weTags = new ArrayList<>();
                        tag.stream().forEach(v -> {
                            WeTag tagInfo = this.getOne(new LambdaQueryWrapper<WeTag>()
                                    .eq(WeTag::getGroupId, k.getGroup_id())
                                    .eq(WeTag::getTagId, v.getId()).last(GenConstants.LIMIT_1));
                            if (tagInfo == null) {
                                WeTag weTag = new WeTag();
                                weTag.setTagId(v.getId());
                                weTag.setGroupId(k.getGroup_id());
                                weTag.setName(v.getName());
                                weTag.setCorpId(corpId);
                                weTag.setCreateTime(new Date(v.getCreate_time() * num));
                                weTags.add(weTag);
                            }
                        });
                        if (CollUtil.isNotEmpty(weTags)) {
                            this.saveOrUpdateBatch(weTags);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void deleteTag(String tagId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(tagId, corpId)) {
            log.error("删除标签失败，tagId：{}，corpId：{}", tagId, corpId);
        }
        this.deleteWeTagById(tagId, corpId);
    }

    @Override
    public void updateTag(String tagId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            log.error("创建标签失败，企业id不能为空");
            throw new BaseException("创建标签失败");
        }
        if (StringUtils.isNotEmpty(tagId)) {
            //使用企微新接口
            WeCropGroupTagListDTO weCropGroupTagListDTO = weCropTagClient.getCorpTagListByTagIds(WeFindCropTagParam.builder().tag_id(tagId.split(",")).build(), corpId);
            List<WeCropGroupTagDTO> tagGroups = weCropGroupTagListDTO.getTag_group();
            if (CollUtil.isNotEmpty(tagGroups)) {
                tagGroups.stream().forEach(k -> {
                    List<WeCropTagDTO> tag = k.getTag();
                    if (CollUtil.isNotEmpty(tag)) {
                        List<WeTag> weTags = new ArrayList<>();
                        tag.stream().forEach(v -> {
                            WeTag weTag = new WeTag();
                            weTag.setTagId(v.getId());
                            weTag.setGroupId(k.getGroup_id());
                            weTag.setName(v.getName());
                            weTag.setCorpId(corpId);
                            weTag.setCreateTime(new Date(v.getCreate_time() * 1000L));
                            weTags.add(weTag);
                        });
                        if (CollUtil.isNotEmpty(weTags)) {
                            this.saveOrUpdateBatch(weTags);
                        }
                    }
                });
            }
        }
    }
}
