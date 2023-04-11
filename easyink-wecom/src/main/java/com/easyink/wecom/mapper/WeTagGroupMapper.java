package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.WeTagGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标签组Mapper接口
 *
 * @author admin
 * @date 2020-09-07
 */
@Repository
public interface WeTagGroupMapper extends BaseMapper<WeTagGroup> {
    /**
     * 查询标签组列表
     *
     * @param weTagGroup 标签组
     * @return 标签组集合
     */
    List<WeTagGroup> selectWeTagGroupList(WeTagGroup weTagGroup);

    /**
     * 根据searchName查询标签组名或标签名为searchName的标签组列表
     *
     * @param searchName
     * @return
     */
    List<WeTagGroup> selectWetagGroupListBySearchName(@Param("searchName") String searchName, @Param("corpId") String corpId);
    /**
     * 批量逻辑删除标签组
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeTagGroupByIds(@Param("array") String[] ids,@Param("corpId") String corpId);

    /**
     * 根据企业员工与添加客户关系id给客户打标签
     *
     * @param flowerCustomerRelId
     * @return
     */
    List<WeTagGroup> findCustomerTagByFlowerCustomerRelId(@Param("flowerCustomerRelId") String flowerCustomerRelId);

    /**
     * 批量插入标签组
     *
     * @param weGroups 标签组集合
     * @return
     */
    Integer batchInsert(@Param("list") List<WeTagGroup> weGroups);
}
