package com.easyink.wecom.mapper.moment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.entity.moment.WeMomentDetailRelEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 朋友圈任务附件关联表
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Repository
public interface WeMomentDetailRelMapper extends BaseMapper<WeMomentDetailRelEntity> {

    /**
     * 查询任务附件
     * @param momentTaskId 任务id
     * @return {@link }
     */
    List<WeWordsDetailEntity> listOfAttachment(@Param("content") String content, @Param("momentTaskId") Long momentTaskId);

}
