package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeEmpleCodeMaterial;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名：WeEmpleCodeMaterialMapper 员工活码素材附件Mapper接口
 *
 * @author 李小琳
 * @date 2021-11-02
 */
@Repository
public interface WeEmpleCodeMaterialMapper extends BaseMapper<WeEmpleCodeMaterial> {

    /**
     * 批量插入话术附件数据
     *
     * @param list list
     * @return int
     */
    int batchInsert(List<WeEmpleCodeMaterial> list);

    /**
     * 根据emplyCodeId和materialId查询素材信息
     *
     * @param emplyCodeId  员工活码ID
     * @param materialSort 素材顺序
     * @return List<WeEmpleCodeMaterial>
     */
    List<WeEmpleCodeMaterial> findByEmplyCodeIdAndMaterialId(@Param("emplyCodeId") Long emplyCodeId, @Param("materialSort") String materialSort);

    /**
     * 根据empleCodeId更新mediaId
     *
     * @param empleCodeId 员工活码ID
     * @param mediaId     群活码ID
     * @return int
     */
    int updateGroupCodeMediaIdByEmpleCodeId(@Param("emplyCodeId") Long empleCodeId, @Param("mediaId") Long mediaId);

    /**
     * 根据emplyCodeId批量删除员工活码附件表
     *
     * @param emplyCodeIdList 员工活码ID列表
     * @return int
     */
    int removeByEmpleCodeId(@Param("emplyCodeIdList") List<Long> emplyCodeIdList);
}
