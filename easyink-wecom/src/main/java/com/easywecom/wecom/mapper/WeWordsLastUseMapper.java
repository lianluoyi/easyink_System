package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeWordsLastUseEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 最近使用话术表
 *
 * @author 佚名
 * @date 2021-11-02 10:32:00
 */
@Repository
public interface WeWordsLastUseMapper extends BaseMapper<WeWordsLastUseEntity> {
    /**
     * 添加
     *
     * @param weWordsLastUseEntity 最近使用话术
     */
    int saveOrUpdate(WeWordsLastUseEntity weWordsLastUseEntity);

    /**
     * 获取最近使用
     *
     * @param userId 员工id（admin 传‘admin’）
     * @param corpId 企业id
     * @param type   话术类型
     * @return {@link WeWordsLastUseEntity}
     */
    WeWordsLastUseEntity getByUserId(@Param("userId") String userId, @Param("corpId") String corpId, @Param("type") Integer type);
}
