package com.easyink.wecom.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeFlowerCustomerTagRel;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.mapper.WeFlowerCustomerTagRelMapper;
import com.easyink.wecom.service.WeFlowerCustomerTagRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户标签关系Service业务层处理
 *
 * @author admin
 * @date 2020-09-19
 */
@Service
public class WeFlowerCustomerTagRelServiceImpl extends ServiceImpl<WeFlowerCustomerTagRelMapper, WeFlowerCustomerTagRel> implements WeFlowerCustomerTagRelService {
    @Autowired
    private WeFlowerCustomerTagRelMapper weFlowerCustomerTagRelMapper;

    /**
     * 查询客户标签关系
     *
     * @param id 客户标签关系ID
     * @return 客户标签关系
     */
    @Override
    public WeFlowerCustomerTagRel selectWeFlowerCustomerTagRelById(Long id) {
        return weFlowerCustomerTagRelMapper.selectWeFlowerCustomerTagRelById(id);
    }

    /**
     * 查询客户标签关系列表
     *
     * @param weFlowerCustomerTagRel 客户标签关系
     * @return 客户标签关系
     */
    @Override
    public List<WeFlowerCustomerTagRel> selectWeFlowerCustomerTagRelList(WeFlowerCustomerTagRel weFlowerCustomerTagRel) {
        return weFlowerCustomerTagRelMapper.selectWeFlowerCustomerTagRelList(weFlowerCustomerTagRel);
    }

    /**
     * 新增客户标签关系
     *
     * @param weFlowerCustomerTagRel 客户标签关系
     * @return 结果
     */
    @Override
    public int insertWeFlowerCustomerTagRel(WeFlowerCustomerTagRel weFlowerCustomerTagRel) {
        weFlowerCustomerTagRel.setCreateTime(DateUtils.getNowDate());
        return weFlowerCustomerTagRelMapper.insertWeFlowerCustomerTagRel(weFlowerCustomerTagRel);
    }

    /**
     * 修改客户标签关系
     *
     * @param weFlowerCustomerTagRel 客户标签关系
     * @return 结果
     */
    @Override
    public int updateWeFlowerCustomerTagRel(WeFlowerCustomerTagRel weFlowerCustomerTagRel) {
        return weFlowerCustomerTagRelMapper.updateWeFlowerCustomerTagRel(weFlowerCustomerTagRel);
    }

    /**
     * 批量删除客户标签关系
     *
     * @param ids 需要删除的客户标签关系ID
     * @return 结果
     */
    @Override
    public int deleteWeFlowerCustomerTagRelByIds(Long[] ids) {
        return weFlowerCustomerTagRelMapper.deleteWeFlowerCustomerTagRelByIds(ids);
    }

    /**
     * 删除客户标签关系信息
     *
     * @param id 客户标签关系ID
     * @return 结果
     */
    @Override
    public int deleteWeFlowerCustomerTagRelById(Long id) {
        return weFlowerCustomerTagRelMapper.deleteWeFlowerCustomerTagRelById(id);
    }


    /**
     * 批量插入
     *
     * @param weFlowerCustomerTagRels
     * @return
     */
    @Override
    public int batchInsetWeFlowerCustomerTagRel(List<WeFlowerCustomerTagRel> weFlowerCustomerTagRels) {
        return weFlowerCustomerTagRelMapper.batchInsetWeFlowerCustomerTagRel(weFlowerCustomerTagRels);
    }

    @Override
    public Integer removeByCustomerIdAndUserId(String externalUserid, String userId, String corpId, List<String> tagList) {
        return weFlowerCustomerTagRelMapper.removeByCustomerIdAndUserId(externalUserid, userId, corpId, tagList);
    }

    @Override
    public Integer removeByCustomerIdAndUserId(String externalUserid, String userId, String corpId) {
        return this.removeByCustomerIdAndUserId(externalUserid, userId, corpId, null);
    }

    @Override
    public Integer removeByRelId(Long relId) {
        return weFlowerCustomerTagRelMapper.removeByRelId(relId);
    }

    @Override
    public void batchInsert(List<WeFlowerCustomerTagRel> tagRelList) {
        weFlowerCustomerTagRelMapper.batchInsert(tagRelList);
    }

    @Override
    public void transferTag(Long handoverRelId, Long takeoverRelId) {
        weFlowerCustomerTagRelMapper.transferTag(handoverRelId, takeoverRelId);
    }

    @Override
    public void setTagForCustomers(String corpId, List<WeCustomerVO> list) {
        if(StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> relList = list.stream().map(WeCustomerVO::getRelId).collect(Collectors.toList());
        // 根据客户-员工关系id 获取其打上的标签
        List<WeFlowerCustomerTagRel> tagRelList = weFlowerCustomerTagRelMapper.getTagRelByRelIds(relList);
        if(CollectionUtils.isEmpty(tagRelList)) {
            return;
        }
        // 根据返回结果进行组装数据
        for(WeCustomerVO vo : list) {
            for(WeFlowerCustomerTagRel tagRel : tagRelList) {
                if(ObjectUtil.equal( vo.getRelId(), tagRel.getFlowerCustomerRelId())) {
                    vo.getWeFlowerCustomerTagRels().add(tagRel);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncTagFromRemote(WeFlowerCustomerRel localRel, List<WeFlowerCustomerTagRel> tagRelList) {
        if(localRel == null || CollectionUtils.isEmpty(tagRelList)) {
            return;
        }
        remove(new LambdaQueryWrapper<WeFlowerCustomerTagRel>()
                .eq(WeFlowerCustomerTagRel::getFlowerCustomerRelId, localRel.getId())
        );
        if (CollectionUtils.isNotEmpty(tagRelList)) {
            BatchInsertUtil.doInsert(tagRelList, this::batchInsert);
        }
    }
}
