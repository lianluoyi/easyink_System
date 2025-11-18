package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.vo.statistics.WeTagGroupListVO;
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
     * 查询单个标签组信息
     *
     * @param weTagGroup 标签组
     * @return 标签组信息
     */
    WeTagGroup selectSingleWeTagGroup(WeTagGroup weTagGroup);

    /**
     * 查询符合条件的标签组列表（数据统计）
     *
     * @param tagGroupIdList 标签组ID列表
     * @param corpId         企业ID
     * @return 标签组集合
     */
    List<WeTagGroup> selectWeTagGroupListByStatistic(@Param("list") List<String> tagGroupIdList, @Param("corpId") String corpId);

    /**
     * 查询所有标签组信息
     *
     * @param corpId 企业ID
     * @return 标签组集合
     */
    List<WeTagGroupListVO> findWeTagGroupList(String corpId);

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

    /**
     * 查询专属活码设定的标签列表
     * @param tagIdList 标签id列表
     * @param groupIdList 标签分组id列表
     * @param corpId 企业id
     * @return
     */
    List<WeTagGroup> selectTagByCustomerLinkTag(@Param("tagIdList") List<String> tagIdList, @Param("groupIdList") List<String> groupIdList, @Param("corpId") String corpId);

}
