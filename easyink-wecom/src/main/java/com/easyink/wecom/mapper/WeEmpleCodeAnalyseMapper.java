package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeEmpleCodeAnalyse;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeAnalyseDTO;
import com.easyink.wecom.domain.vo.WeEmplyCodeAnalyseCountVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名：WeEmpleCodeAnalyseMapper
 *
 * @author Society my sister Li
 * @date 2021-11-04
 */
@Repository
public interface WeEmpleCodeAnalyseMapper extends BaseMapper<WeEmpleCodeAnalyse> {
    /**
     * 根据活码Id获取员工Id列表
     *
     * @param corpId 企业ID
     * @param empleCodeId 活码ID
     * @return userId列表
     */
    List<String> selectUserIdsById(@Param("corpId") String corpId, @Param("empleCodeId") Long empleCodeId);

    /**
     * 查询
     *
     * @param analyseDTO analyseDTO
     * @return List<WeEmpleCodeAnalyse>
     */
    List<WeEmpleCodeAnalyse> selectAnalyseList(FindWeEmpleCodeAnalyseDTO analyseDTO);

    /**
     * 查询每个日期的新增和流失数量
     *
     * @param analyseDTO analyseDTO
     * @return List<WeEmplyCodeAnalyseCountVO>
     */
    List<WeEmplyCodeAnalyseCountVO> selectCountList(FindWeEmpleCodeAnalyseDTO analyseDTO);

    /**
     * 新增
     *
     * @param weEmpleCodeAnalyse weEmpleCodeAnalyse
     * @return int
     */
    int insert(WeEmpleCodeAnalyse weEmpleCodeAnalyse);

    /**
     * 获取企业下所有活码-员工对应的累计客户数
     *
     * @param corpId 企业ID
     * @param date 日期 格式为：YYYY-MM-DD
     * @param empleCodeIdList 活码ID列表
     * @return 统计数据
     */
    List<WeEmpleCodeStatistic> getAccumulateData(@Param("corpId") String corpId, @Param("date") String date, @Param("empleCodeIdList") List<Long> empleCodeIdList);

    /**
     * 获取企业下所有活码-员工对应的留存客户数总数
     *
     * @param corpId 企业ID
     * @param date 日期 格式为：YYYY-MM-DD
     * @param empleCodeIdList 活码ID列表
     * @return 统计数据
     */
    List<WeEmpleCodeStatistic> getRetainData(@Param("corpId") String corpId, @Param("date") String date, @Param("empleCodeIdList") List<Long> empleCodeIdList);

    /**
     * 获取企业下所有活码-员工对应的新增客户数和流失客户数
     *
     * @param corpId 企业ID
     * @param date 日期 格式为：YYYY-MM-DD
     * @return 统计数据
     */
    List<WeEmpleCodeStatistic> getEmpleStatisticDateData(@Param("corpId") String corpId, @Param("date") String date, @Param("empleCodeIdList") List<Long> empleCodeIdList);
}
