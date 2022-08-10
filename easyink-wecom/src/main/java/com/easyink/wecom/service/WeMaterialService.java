package com.easyink.wecom.service;

import com.easyink.wecom.domain.WeMaterial;
import com.easyink.wecom.domain.dto.*;
import com.easyink.wecom.domain.vo.InsertWeMaterialVO;
import com.easyink.wecom.domain.vo.WeMaterialCountVO;
import com.easyink.wecom.domain.vo.WeMaterialFileVO;
import com.easyink.wecom.domain.vo.WeMaterialVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 素材service
 *
 * @author admin
 * @date 2020-10-08
 */
public interface WeMaterialService {

    /**
     * 上传素材信息
     *
     * @param file 文件
     * @param type 0 图片（image）、1 语音（voice）、2 视频（video），3 普通文件(file)
     * @return {@link WeMaterialFileVO}
     */
    WeMaterialFileVO uploadWeMaterialFile(MultipartFile file, String type);

    /**
     * 添加素材信息
     *
     * @param weMaterial 素材信息
     * @return {@link InsertWeMaterialVO}
     */
    InsertWeMaterialVO insertWeMaterial(AddWeMaterialDTO weMaterial);

    /**
     * 批量插入数据
     *
     * @param list list
     * @return 受影响行
     */
    int batchInsertWeMaterial(List<WeMaterial> list);

    /**
     * 批量删除
     *
     * @param removeMaterialDTO removeMaterialDTO
     */
    void deleteWeMaterialByIds(RemoveMaterialDTO removeMaterialDTO);

    /**
     * 更新素材信息
     *
     * @param weMaterial
     * @return 受影响行
     */
    int updateWeMaterial(UpdateWeMaterialDTO weMaterial);

    /**
     * 查询素材详细信息
     *
     * @param id id
     * @return {@link WeMaterial}
     */
    WeMaterial findWeMaterialById(Long id);

    /**
     * 查询素材列表
     *
     * @param findWeMaterialDTO 搜索条件
     * @return {@link WeMaterialVO}s
     */
    List<WeMaterialVO> findWeMaterials(FindWeMaterialDTO findWeMaterialDTO);

    /**
     * 更换分组
     *
     * @param categoryId 类目id
     * @param materials  素材id
     */
    void resetCategory(String categoryId, String materials);


    /**
     * 上传企微临时素材
     *
     * @param url    素材路径
     * @param type   素材类型
     * @param name   素材名称
     * @param corpId 企业ID
     * @return WeMediaDTO
     */
    WeMediaDTO uploadTemporaryMaterial(String url, String type, String name, String corpId);

    /**
     * @param url            素材路径
     * @param mediaType      素材类型
     * @param attachmentType 附件类型 1：朋友圈；2:商品图册
     * @param name           素材名称
     * @param corpId         企业ID
     * @return
     */
    WeMediaDTO uploadAttachment(String url, String mediaType, Integer attachmentType, String name, String corpId);

    /**
     * 上传素材图片
     *
     * @param file
     * @return
     */
    WeMediaDTO uploadImg(MultipartFile file, String corpId);


    /**
     * 素材发布/批量发布
     *
     * @param showMaterialSwitchDTO showMaterialSwitchDTO
     */
    void showMaterialSwitch(ShowMaterialSwitchDTO showMaterialSwitchDTO);

    /**
     * 过期素材恢复
     *
     * @param restoreMaterialDTO restoreMaterialDTO
     */
    void restore(RestoreMaterialDTO restoreMaterialDTO);

    /**
     * 过期素材定时移除
     *
     * @param corpId         企业ID
     * @param lastRemoveDate 最后可移除的时间
     */
    void removeMaterialByJob(String corpId, Date lastRemoveDate);

    /**
     * 查询素材数量、发布到侧边栏数量
     *
     * @param findWeMaterialDTO 筛选条件
     * @return {@link WeMaterialCountVO}
     */
    WeMaterialCountVO getMaterialCount(FindWeMaterialDTO findWeMaterialDTO);

    /**
     * 根据materialSort查询数据并排序
     *
     * @param materialSort 已排序的素材ID
     * @param corpId       企业ID
     * @return List<AddWeMaterialDTO>
     */
    List<AddWeMaterialDTO> getListByMaterialSort(String[] materialSort, String corpId);

    /**
     * 根据materialSort查询数据并排序
     *
     * @param codeSuccessMaterialSort 已排序的素材ID
     * @param corpId                  企业ID
     * @return List<AddWeMaterialDTO>
     */
    List<AddWeMaterialDTO> getRedeemCodeListByMaterialSort(String[] codeSuccessMaterialSort, String corpId);
}
