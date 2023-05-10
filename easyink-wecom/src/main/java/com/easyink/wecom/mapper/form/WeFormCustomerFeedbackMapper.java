package com.easyink.wecom.mapper.form;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.dto.statistics.GoodCommitDTO;
import com.easyink.wecom.domain.dto.statistics.UserGoodReviewDTO;
import com.easyink.wecom.domain.dto.statistics.UserServiceDTO;
import org.apache.ibatis.annotations.Param;
import com.easyink.wecom.domain.entity.form.WeFormCustomerFeedback;

/**
 * 客户好评评价表(WeFormCustomerFeedback)表数据库访问层
 *
 * @author tigger
 * @since 2023-01-13 16:10:13
 */
public interface WeFormCustomerFeedbackMapper extends BaseMapper<WeFormCustomerFeedback> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormCustomerFeedback> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<WeFormCustomerFeedback> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<WeFormCustomerFeedback> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<WeFormCustomerFeedback> entities);

    /**
     * 查询好评
     *
     * @param dto {@link GoodCommitDTO}
     * @return {@link UserGoodReviewDTO}
     */
    List<UserGoodReviewDTO> selectGoodReviews(GoodCommitDTO dto);

    /**
     * 查询指定时间所有人的好评
     *
     * @param dto {@link GoodCommitDTO}
     * @return
     */
    List<UserGoodReviewDTO> selsectGoodReviewsForPerson(GoodCommitDTO dto);

    /**
     * 查询时间维度所需评价数据
     *
     * @param dto {@link GoodCommitDTO}
     * @return {@link UserGoodReviewDTO}
     */
    List<UserGoodReviewDTO> selectGoodReviewsForTime(GoodCommitDTO dto);
}

