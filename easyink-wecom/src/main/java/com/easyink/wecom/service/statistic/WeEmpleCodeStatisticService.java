package com.easyink.wecom.service.statistic;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.dto.statistics.EmpleCodeStatisticDTO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeBaseVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeDateVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeUserVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeVO;

import java.util.List;


/**
 * 活码统计功能（we_emple_code_statistic）表服务接口
 *
 * @author lichaoyu
 * @date 2023/7/5 13:43
 */
public interface WeEmpleCodeStatisticService extends IService<WeEmpleCodeStatistic> {

    /**
     * 活码统计-数据总览
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeBaseVO}
     */
    EmpleCodeBaseVO listEmpleTotal(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-员工维度
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeUserVO> listEmpleUser(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-员工维度-导出报表
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 报表
     */
    AjaxResult exportEmpleUser(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-活码维度
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeVO> listEmple(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-活码维度-导出报表
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 报表
     */
    AjaxResult exportEmple(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-日期维度
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    List<EmpleCodeDateVO> listEmpleDate(EmpleCodeStatisticDTO dto);

    /**
     * 活码统计-日期维度-导出报表
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 报表
     */
    AjaxResult exportEmpleDate(EmpleCodeStatisticDTO dto);
}
