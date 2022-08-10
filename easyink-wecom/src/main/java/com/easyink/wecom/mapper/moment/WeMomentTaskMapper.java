package com.easyink.wecom.mapper.moment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.dto.moment.MomentUserCustomerDTO;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskEntity;
import com.easyink.wecom.domain.vo.moment.MomentTotalVO;
import com.easyink.wecom.domain.vo.moment.MomentUserCustomerVO;
import com.easyink.wecom.domain.vo.moment.SearchMomentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 朋友圈任务信息表
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Repository
public interface WeMomentTaskMapper extends BaseMapper<WeMomentTaskEntity> {
    /**
     * 查询未发布的朋友圈任务
     * @return {@link List<WeMomentTaskEntity>}
     */
    List<WeMomentTaskEntity> listOfNotPublish(@Param("subDay") Date subDay, @Param("isExpire") Boolean isExpire);

    /**
     * 查询定时任务
     * @param now 当前时间
     * @return {@link List<WeMomentTaskEntity>}
     */
    List<WeMomentTaskEntity> listOfSettingTask(@Param("now") Date now);

    /**
     * 查询朋友圈列表
     * @param userIds 权限下员工id
     * @param corpId 企业id
     * @param content 内容
     * @param endTime 结束时间
     * @param beginTime 开始时间
     * @param type 朋友圈类型
     * @return {@link List<SearchMomentVO>}
     */
    List<SearchMomentVO> listOfMomentTask(@Param("list") List<String> userIds, @Param("corpId") String corpId, @Param("content") String content, @Param("endTime") String endTime, @Param("beginTime") String beginTime, @Param("type") Integer type);

    /**
     * 查询员工执行朋友圈详情
     * @param momentUserCustomerDTO 参数
     * @return {@link List<MomentUserCustomerVO>}
     */
    List<MomentUserCustomerVO> listOfMomentPublishDetail(MomentUserCustomerDTO momentUserCustomerDTO);

    /**
     * 获得统计详情
     * @param momentTaskId 任务id
     * @return {@link MomentTotalVO}
     */
    MomentTotalVO getTotal(@Param("momentTaskId") Long momentTaskId);

    /**
     * 获得详情基础信息
     * @param momentTaskId 任务id
     * @return {@link SearchMomentVO}
     */
    SearchMomentVO getMomentTaskBasicInfo(@Param("momentTaskId") Long momentTaskId,@Param("corpId") String corpId);

    /**
     * h5 获得员工朋友圈
     * @param momentTaskId 任务id
     * @param userId 员工id
     * @return {@link SearchMomentVO}
     */
    SearchMomentVO getMomentTaskByUserIdMomentId(@Param("momentTaskId") Long momentTaskId,@Param("userId") String userId);
}
