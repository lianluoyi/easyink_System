package com.easyink.wecom.service.redeemcode;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDTO;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDeleteDTO;
import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCode;
import com.easyink.wecom.domain.vo.redeemcode.ImportRedeemCodeVO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * ClassName： WeRedeemCodeService
 *
 * @author wx
 * @date 2022/7/5 17:22
 */
public interface WeRedeemCodeService extends IService<WeRedeemCode> {

    /**
     * 上传兑换码库存
     *
     * @param corpId
     * @param file
     * @param id     活动码id
     * @return
     */
    ImportRedeemCodeVO importRedeemCode(String corpId, MultipartFile file, String id) throws IOException;

    /**
     * 新增兑换码
     *
     * @param weRedeemCodeDTO
     */
    void saveRedeemCode(WeRedeemCodeDTO weRedeemCodeDTO);

    /**
     * 编辑修改兑换码
     *
     * @param weRedeemCodeDTO
     */
    void updateRedeemCode(WeRedeemCodeDTO weRedeemCodeDTO);

    /**
     * 批量删除兑换码
     *
     * @param deleteDTO
     * @return
     */
    int batchRemoveRedeemCode(WeRedeemCodeDeleteDTO deleteDTO);

    /**
     * 查询兑换码列表
     *
     * @param weRedeemCodeDTO
     * @return
     */
    List<WeRedeemCodeVO> getReemCodeList(WeRedeemCodeDTO weRedeemCodeDTO);

}
