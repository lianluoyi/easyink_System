package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
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


    /**
     * 批量插入或更新扩展属性值
     *
     * @param list 拓展属性值列表
     * @return 结果
     */
    Integer batchSaveOrUpdate(@Param("list") List<ExtendPropertyMultipleOption> list);
}
