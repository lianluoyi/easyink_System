package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.WeCategoryMediaTypeEnum;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.easywecom.wecom.domain.WeWordsDetailEntity;
import com.easywecom.wecom.domain.vo.sop.SopAttachmentVO;
import com.easywecom.wecom.mapper.WeWordsDetailMapper;
import com.easywecom.wecom.service.WeWordsDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类名： 话术库附件接口
 *
 * @author 佚名
 * @date 2021/10/27 16:05
 */
@Service
@Slf4j
public class WeWordsDetailServiceImpl extends ServiceImpl<WeWordsDetailMapper, WeWordsDetailEntity> implements WeWordsDetailService {

    private static final int LINK_TITLE_SIZE = 64;
    private static final int ATTACHMENT_TITLE_SIZE = 32;

    private WeWordsDetailMapper weWordsDetailMapper;

    @Autowired
    public WeWordsDetailServiceImpl(WeWordsDetailMapper weWordsDetailMapper) {
        this.weWordsDetailMapper = weWordsDetailMapper;
    }

    /**
     * 插入或更新
     *
     * @param wordsDetailEntities 实体列表
     */
    @Override
    public void saveOrUpdate(List<WeWordsDetailEntity> wordsDetailEntities) {
        buildWordsDetails(wordsDetailEntities);
        weWordsDetailMapper.batchInsertOrUpdate(wordsDetailEntities);
    }

    @Override
    public void saveOrUpdate(List<WeWordsDetailEntity> attachments, Boolean isWordsDetail, String corpId) {
        if (CollectionUtils.isEmpty(attachments)|| isWordsDetail || StringUtils.isBlank(corpId)) {
            log.error("保存非话术附件失败参数不合法，isWordsDetail:{},corpId:{},attachments:{}",isWordsDetail,corpId,attachments);
            return;
        }
        //保存任务附件 非话术库的话术分组设置为-1
        attachments.forEach(attachment -> {
            attachment.setGroupId(-1L);
            attachment.setId(SnowFlakeUtil.nextId());
            attachment.setCorpId(corpId);
        });
        this.saveOrUpdate(attachments);
    }


    @Override
    public void deleteByGroupId(Long groupId) {
        if (groupId == null) {
            throw new CustomException(ResultTip.TIP_MISS_WORDS_GROUP_ID);
        }
        weWordsDetailMapper.delete(new LambdaQueryWrapper<WeWordsDetailEntity>().eq(WeWordsDetailEntity::getGroupId, groupId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delByCorpIdAndIdList(String corpId, List<Long> idList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(idList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeWordsDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeWordsDetailEntity::getCorpId, corpId)
                .in(WeWordsDetailEntity::getId, idList);
        baseMapper.delete(wrapper);
    }

    @Override
    public List<SopAttachmentVO> listOfRuleId(Long ruleId) {
        return baseMapper.listOfRuleId(ruleId);
    }

    /**
     * 保存朋友圈任务附件
     *
     * @param attachments 附件
     */
    @Override
    public void saveMomentDetail(List<WeWordsDetailEntity> attachments) {
        //保存任务附件
        if (CollectionUtils.isNotEmpty(attachments)) {
            //非话术库的话术分组设置为-1
            attachments.forEach(attachment -> {
                attachment.setGroupId(-1L);
                attachment.setId(SnowFlakeUtil.nextId());
            });
            this.saveOrUpdate(attachments);
        }
    }

    /**
     * 构造附件，填充ID和默认值
     *
     * @param wordsDetailEntities 附件
     */
    private void buildWordsDetails(List<WeWordsDetailEntity> wordsDetailEntities) {
        wordsDetailEntities.forEach(weWordsDetailEntity -> {
            checkDetail(weWordsDetailEntity);
            if (weWordsDetailEntity.getId() == null) {
                weWordsDetailEntity.setId(SnowFlakeUtil.nextId());
            }
            if (weWordsDetailEntity.getContent() == null) {
                weWordsDetailEntity.setContent(StringUtils.EMPTY);
            }
            if (weWordsDetailEntity.getCoverUrl() == null) {
                weWordsDetailEntity.setCoverUrl(StringUtils.EMPTY);
            }
            if (weWordsDetailEntity.getUrl() == null) {
                weWordsDetailEntity.setUrl(StringUtils.EMPTY);
            }
            if (weWordsDetailEntity.getTitle() == null) {
                weWordsDetailEntity.setTitle(StringUtils.EMPTY);
            }
            if (weWordsDetailEntity.getGroupId() == null) {
                weWordsDetailEntity.setGroupId(0L);
            }
            if (weWordsDetailEntity.getIsDefined() == null) {
                weWordsDetailEntity.setIsDefined(Boolean.FALSE);
            }
            if (weWordsDetailEntity.getSize() == null) {
                weWordsDetailEntity.setSize(0L);
            }
            if (weWordsDetailEntity.getRadarId() == null) {
                weWordsDetailEntity.setRadarId(0L);
            }
        });
    }

    /**
     * 校验话术附件完整性
     *
     * @param weWordsDetailEntity 话术附件
     */
    private void checkDetail(WeWordsDetailEntity weWordsDetailEntity) {
        if (WeConstans.WE_WORDS_DETAIL_MEDIATYPE_TEXT.equals(weWordsDetailEntity.getMediaType())) {
            if (StringUtils.isBlank(weWordsDetailEntity.getContent())) {
                throw new CustomException(ResultTip.TIP_MISS_WORDS_ATTACHMENT_CONTENT);
            }
        } else if (WeCategoryMediaTypeEnum.MINI_APP.getMediaType().equals(weWordsDetailEntity.getMediaType())) {
            if (StringUtils.isBlank(weWordsDetailEntity.getContent()) || StringUtils.isBlank(weWordsDetailEntity.getTitle()) || StringUtils.isBlank(weWordsDetailEntity.getUrl())) {
                throw new CustomException(ResultTip.TIP_MISS_WORDS_ATTACHMENT_CONTENT);
            }
        } else {
            if (StringUtils.isBlank(weWordsDetailEntity.getUrl())) {
                throw new CustomException(ResultTip.TIP_MISS_WORDS_ATTACHMENT_CONTENT);
            }
            //缺少标题
            if (StringUtils.isBlank(weWordsDetailEntity.getTitle())) {
                throw new CustomException(ResultTip.TIP_MISS_WORDS_ATTACHMENT_CONTENT);
            }
            if (WeCategoryMediaTypeEnum.LINK.getMediaType().equals(weWordsDetailEntity.getMediaType())) {
                if (StringUtils.isNotBlank(weWordsDetailEntity.getTitle()) && weWordsDetailEntity.getTitle().length() > LINK_TITLE_SIZE) {
                    throw new CustomException(ResultTip.TIP_WORDS_OVER_TITLE);
                }
            } else if (weWordsDetailEntity.getTitle().length() > ATTACHMENT_TITLE_SIZE) {
                throw new CustomException(ResultTip.TIP_WORDS_OVER_TITLE);
            }

            if (WeCategoryMediaTypeEnum.RADAR.getMediaType().equals(weWordsDetailEntity.getMediaType())) {
                if (StringUtils.isNotBlank(weWordsDetailEntity.getTitle()) && weWordsDetailEntity.getTitle().length() > LINK_TITLE_SIZE) {
                    throw new CustomException(ResultTip.TIP_WORDS_OVER_TITLE);
                }
            } else if (weWordsDetailEntity.getTitle().length() > ATTACHMENT_TITLE_SIZE) {
                throw new CustomException(ResultTip.TIP_WORDS_OVER_TITLE);
            }

        }
    }
}