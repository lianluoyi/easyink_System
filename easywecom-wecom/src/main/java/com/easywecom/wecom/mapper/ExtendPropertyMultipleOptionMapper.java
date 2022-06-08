package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: 客户自定义属性多选值数据库映射
 *
 * @author : silver_chariot
 * @date : 2021/11/15 13:43
 */
@Repository
public interface ExtendPropertyMultipleOptionMapper extends BaseMapper<ExtendPropertyMultipleOption> {
    /**
     * 批量更新
     *
     * @param list {@link List<ExtendPropertyMultipleOption>}
     * @return
     */
    Integer insertOrUpdate(@Param("list") List<ExtendPropertyMultipleOption> list);
}
