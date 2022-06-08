package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeEmpleCodeAnalyse;
import com.easywecom.wecom.domain.dto.emplecode.FindWeEmpleCodeAnalyseDTO;
import com.easywecom.wecom.domain.vo.WeEmplyCodeAnalyseVO;

/**
 * 类名：WeEmpleCodeAnalyseService
 *
 * @author Society my sister Li
 * @date 2021-11-04
 */
public interface WeEmpleCodeAnalyseService extends IService<WeEmpleCodeAnalyse> {

    /**
     * 时间段内新增和流失客户数据统计
     *
     * @param findWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO
     * @return List<WeEmpleCodeAnalyse>
     */
    WeEmplyCodeAnalyseVO getTimeRangeAnalyseCount(FindWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO);

    /**
     * 新增
     *
     * @param corpId         企业ID
     * @param userId         员工ID
     * @param externalUserId 客户ID
     * @param state          state
     * @return boolean
     */
    boolean saveWeEmpleCodeAnalyse(String corpId, String userId, String externalUserId, String state, Boolean addFlag);

    /**
     * 根据state查询添加活码数
     *
     * @param state state
     * @return int
     */
    int getAddCountByState(String state);
}
