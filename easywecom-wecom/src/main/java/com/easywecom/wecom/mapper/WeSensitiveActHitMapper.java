package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.common.core.domain.RootEntity;
import com.easywecom.wecom.domain.WeSensitiveActHit;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名： WeSensitiveActHitMapper
 *
 * @author 佚名
 * @date 2021/8/31 11:03
 */
@Repository
public interface WeSensitiveActHitMapper extends BaseMapper<WeSensitiveActHit> {
    /**
     * 查询可见部门的成员敏感行为
     *
     * @param rootEntity
     * @return 敏感行为
     */
    List<WeSensitiveActHit> listOfWeSensitiveActHit(RootEntity rootEntity);
}
