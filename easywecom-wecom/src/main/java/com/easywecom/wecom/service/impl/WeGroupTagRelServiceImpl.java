package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeGroupTagRel;
import com.easywecom.wecom.domain.dto.wegrouptag.BatchTagRelDTO;
import com.easywecom.wecom.domain.vo.wegrouptag.WeGroupTagRelVO;
import com.easywecom.wecom.mapper.WeGroupTagRelMapper;
import com.easywecom.wecom.service.WeGroupTagRelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：WeGroupTagRelServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
@Service
public class WeGroupTagRelServiceImpl extends ServiceImpl<WeGroupTagRelMapper, WeGroupTagRel> implements WeGroupTagRelService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddTagRel(BatchTagRelDTO batchTagRelDTO) {
        verifyParam(batchTagRelDTO);
        String corpId = batchTagRelDTO.getCorpId();
        WeGroupTagRel tagRel;
        List<WeGroupTagRel> addList = new ArrayList<>();

        //遍历给每个群打标签
        for (String chatId : batchTagRelDTO.getChatIdList()) {
            for (Long tagId : batchTagRelDTO.getTagIdList()) {
                tagRel = new WeGroupTagRel(corpId, chatId, tagId);
                addList.add(tagRel);
            }
        }
        return baseMapper.batchAddTag(addList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelTagRel(BatchTagRelDTO batchTagRelDTO) {
        verifyParam(batchTagRelDTO);
        return baseMapper.batchDelTag(batchTagRelDTO.getCorpId(), batchTagRelDTO.getChatIdList(), batchTagRelDTO.getTagIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delByTagIdList(String corpId, List<Long> tagIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(tagIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.delByTagIdList(corpId, tagIdList);
    }

    @Override
    public List<WeGroupTagRelVO> getByChatIdList(String corpId, List<String> chatIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(chatIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.getByChatIdList(corpId, chatIdList);
    }

    /**
     * 校验请求参数
     *
     * @param batchTagRelDTO batchTagRelDTO
     */
    private void verifyParam(BatchTagRelDTO batchTagRelDTO) {
        if (batchTagRelDTO == null
                || StringUtils.isBlank(batchTagRelDTO.getCorpId())
                || CollectionUtils.isEmpty(batchTagRelDTO.getChatIdList())
                || CollectionUtils.isEmpty(batchTagRelDTO.getTagIdList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }
}
