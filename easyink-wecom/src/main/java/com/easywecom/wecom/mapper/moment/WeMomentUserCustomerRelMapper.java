package com.easywecom.wecom.mapper.moment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.moment.WeMomentUserCustomerRelEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 朋友圈客户员工关联表
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Repository
public interface WeMomentUserCustomerRelMapper extends BaseMapper<WeMomentUserCustomerRelEntity> {

    /**
     * 保存（跳过重复的键）
     * @param userCustomerRelList 插入的列表
     */
    void saveIgnoreDuplicateKey(@Param("list") List<WeMomentUserCustomerRelEntity> userCustomerRelList);
}
