package com.easyink.wecom.mapper.redeemcode;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDTO;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeImportDTO;
import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCode;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassName： WeRedeemCodeMapper
 *
 * @author wx
 * @date 2022/7/5 18:09
 */
@Mapper
@Repository
public interface WeRedeemCodeMapper extends BaseMapper<WeRedeemCode> {

    /**
     * 分页查询兑换码
     *
     * @param weRedeemCodeDTO
     * @return
     */
    List<WeRedeemCodeVO> selectWeRedeemCodeList(WeRedeemCodeDTO weRedeemCodeDTO);

    /**
     * 插入兑换码
     *
     * @param weRedeemCode
     */
    void insertWeRedeemCode(WeRedeemCode weRedeemCode);

    /**
     * 批量插入兑换码
     *
     * @param redeemCodeImport
     */
    void batchInsert(@Param("list") List<WeRedeemCodeImportDTO> redeemCodeImport);

    /**
     * 更新兑换码
     *
     * @param weRedeemCode
     */
    void updateWeRedeemCode(WeRedeemCode weRedeemCode);

    /**
     * 查找兑换码
     *
     * @param weRedeemCode
     * @return
     */
    WeRedeemCode selectOne(WeRedeemCode weRedeemCode);

    /**
     * 查询在有效期内的兑换码
     *
     * @param weRedeemCodeDTO
     * @return
     */
    WeRedeemCode selectOneWhenInEffective(WeRedeemCode weRedeemCodeDTO);

    /**
     * 通过id查询兑换码码
     *
     * @param id
     * @return
     */
    List<WeRedeemCode> listWeRedeemCode(String id);
}
