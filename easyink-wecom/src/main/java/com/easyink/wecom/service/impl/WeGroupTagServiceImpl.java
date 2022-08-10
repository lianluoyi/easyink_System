package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.WeGroupTag;
import com.easyink.wecom.mapper.WeGroupTagMapper;
import com.easyink.wecom.service.WeGroupTagRelService;
import com.easyink.wecom.service.WeGroupTagService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名：WeGroupTagServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
@Service
public class WeGroupTagServiceImpl extends ServiceImpl<WeGroupTagMapper, WeGroupTag> implements WeGroupTagService {

    private final WeGroupTagRelService weGroupTagRelService;

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

}
