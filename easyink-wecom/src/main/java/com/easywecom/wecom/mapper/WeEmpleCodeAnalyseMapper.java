package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeEmpleCodeAnalyse;
import com.easywecom.wecom.domain.dto.emplecode.FindWeEmpleCodeAnalyseDTO;
import com.easywecom.wecom.domain.vo.WeEmplyCodeAnalyseCountVO;
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
     * 查询
     *
     * @param analyseDTO analyseDTO
     * @return List<WeEmpleCodeAnalyse>
     */
    List<WeEmpleCodeAnalyse> selectList(FindWeEmpleCodeAnalyseDTO analyseDTO);

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
}
