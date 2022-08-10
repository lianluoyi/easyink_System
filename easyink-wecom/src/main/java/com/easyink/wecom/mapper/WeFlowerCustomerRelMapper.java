package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 具有外部联系人功能企业员工也客户的关系Mapper接口
 *
 * @author admin
 * @date 2020-09-19
 */
@Repository
public interface WeFlowerCustomerRelMapper extends BaseMapper<WeFlowerCustomerRel> {

    /**
     * 新增具有外部联系人功能企业员工也客户的关系
     *
     * @param weFlowerCustomerRel 具有外部联系人功能企业员工也客户的关系
     * @return 结果
     */
    int insertWeFlowerCustomerRel(WeFlowerCustomerRel weFlowerCustomerRel);

    /**
     * 批量删除具有外部联系人功能企业员工也客户的关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeFlowerCustomerRelByIds(Long[] ids);



    /**
     * 批量添加或修改客户关系
     * @param weFlowerCustomerRels
     */
    int myBatchUpdateOrInsert(List<WeFlowerCustomerRel> weFlowerCustomerRels);

    /**
     * 成员添加客户统计
     *
     * @param weFlowerCustomerRel
     * @return
     */
    List<Map<String, Object>> getUserAddCustomerStat(WeFlowerCustomerRel weFlowerCustomerRel);

    /**
     * 批量插入
     *
     * @param list 集合
     * @return 更新行数
     */
    Integer batchInsert(@Param("list") List<WeFlowerCustomerRel> list);

    /**
     * 批量更新或者修改
     *
     * @param entity {@link WeFlowerCustomerRel}
     * @return 修改行数
     */
    Integer saveOrUpdate(WeFlowerCustomerRel entity);

    /**
     * 转接员工客户关系
     *
     * @param corpId         公司ID
     * @param handoverRelId  原跟进人客户关系id
     * @param takeoverRelId  接替人客户关系id
     * @param takeoverUserId
     */
    void transferRel(@Param("corpId") String corpId, @Param("handoverRelId") Long handoverRelId, @Param("takeoverRelId") Long takeoverRelId, @Param("takeoverUserid") String takeoverUserId);

    /**
     * 批量更新状态
     *
     * @param relList {@link List<WeFlowerCustomerRel>}
     */
    void batchUpdateStatus(@Param("list") List<WeFlowerCustomerRel> relList);
}
