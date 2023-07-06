package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeCategoryMediaTypeEnum;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.wecom.client.WeMediaClient;
import com.easyink.wecom.domain.WeMaterial;
import com.easyink.wecom.domain.dto.*;
import com.easyink.wecom.domain.vo.InsertWeMaterialVO;
import com.easyink.wecom.domain.vo.WeMaterialCountVO;
import com.easyink.wecom.domain.vo.WeMaterialFileVO;
import com.easyink.wecom.domain.vo.WeMaterialVO;
import com.easyink.wecom.mapper.WeMaterialMapper;
import com.easyink.wecom.service.WeMaterialService;
import com.easyink.wecom.service.WeMaterialTagRelService;
import com.easyink.wecom.service.WeMaterialTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 素材service
 *
 * @author admin
 * @date 2020-10-08
 */
@Slf4j
@Service
public class WeMaterialServiceImpl extends ServiceImpl<WeMaterialMapper, WeMaterial> implements WeMaterialService {
    private final WeMaterialMapper weMaterialMapper;
    private final WeMediaClient weMediaClient;
    private final RuoYiConfig ruoYiConfig;
    private final WeMaterialTagService weMaterialTagService;
    private final WeMaterialTagRelService weMaterialTagRelService;

    @Autowired
    public WeMaterialServiceImpl(@NotNull WeMaterialMapper weMaterialMapper, @NotNull WeMediaClient weMediaClient, @NotNull RuoYiConfig ruoYiConfig, @NotNull WeMaterialTagService weMaterialTagService, @NotNull WeMaterialTagRelService weMaterialTagRelService) {
        this.ruoYiConfig = ruoYiConfig;
        this.weMediaClient = weMediaClient;
        this.weMaterialMapper = weMaterialMapper;
        this.weMaterialTagService = weMaterialTagService;
        this.weMaterialTagRelService = weMaterialTagRelService;
    }

    @Override
    public WeMaterialFileVO uploadWeMaterialFile(MultipartFile file, String type) {
        if (null == file) {
            throw new WeComException("文件为空！");
        }
        try {
            //上传临时素材
            Optional<com.easyink.common.enums.MediaType> mediaType = com.easyink.common.enums.MediaType.of(type);
            if (!mediaType.isPresent()) {
                throw new WeComException(ResultTip.TIP_MEDIA_TYPE_ERROR.getTipMsg());
            }
            //构造返回结果
            String cosImgUrlPrefix = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix();
            return WeMaterialFileVO.builder().materialUrl(cosImgUrlPrefix).materialName(FileUploadUtils.upload2Cos(file, ruoYiConfig.getFile().getCos())).build();
        } catch (Exception e) {
            throw new WeComException(e.getMessage());
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertWeMaterialVO insertWeMaterial(AddWeMaterialDTO weMaterial) {
        validWeMaterialParam(weMaterial, weMaterial.getMediaType());
        weMaterial.setCreateTime(DateUtil.date());
        weMaterial.setId(SnowFlakeUtil.nextId());
        //打素材标签
        if (!CollectionUtils.isEmpty(weMaterial.getTagIdList())) {
            List<Long> materialList = new ArrayList<>();
            materialList.add(weMaterial.getId());
            weMaterialTagService.markTags(materialList, weMaterial.getTagIdList());
        }
        weMaterialMapper.insertWeMaterial(weMaterial);
        return new InsertWeMaterialVO(weMaterial.getId());
    }

    @Override
    public int batchInsertWeMaterial(List<WeMaterial> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return weMaterialMapper.batchInsertWeMaterial(list);
    }

    /**
     * 校验请求参数
     *
     * @param weMaterial weMaterial
     */
    private void validWeMaterialParam(WeMaterial weMaterial, Integer mediaType) {
        // 如果是额外的素材 如雷达、表单 则不需要设置materialUrl 和 materialName
        boolean isExistMaterialBaseInfo = (WeCategoryMediaTypeEnum.FORM.getMediaType().equals(mediaType) || WeCategoryMediaTypeEnum.RADAR.getMediaType().equals(mediaType))
                || !StringUtils.isAnyBlank(weMaterial.getMaterialName(), weMaterial.getMaterialUrl());
        if (weMaterial.getCategoryId() == null || mediaType == null
                || !isExistMaterialBaseInfo) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        //小程序需增加校验
        if (WeCategoryMediaTypeEnum.MINI_APP.getMediaType().equals(mediaType)
                && (StringUtils.isAnyBlank(weMaterial.getAppid(), weMaterial.getCoverUrl(), weMaterial.getAccountOriginalId()))) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //校验过期时间
        if (!StringUtils.isBlank(weMaterial.getExpireTime())) {
            Date expireTime;
            try {
                expireTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, weMaterial.getExpireTime());
            } catch (Exception e) {
                throw new CustomException(ResultTip.TIP_EXPIRETIME_DATA_ERROR);
            }

            long diffTime = DateUtils.diffTime(new Date(), expireTime);
            if (diffTime >= 0) {
                throw new CustomException(ResultTip.TIP_EXPIRETIME_LESS_CURR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWeMaterialByIds(RemoveMaterialDTO removeMaterialDTO) {
        if (removeMaterialDTO == null || StringUtils.isBlank(removeMaterialDTO.getIds())
                || StringUtils.isBlank(removeMaterialDTO.getCorpId()) || removeMaterialDTO.getMediaType() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        weMaterialMapper.deleteByIdList(removeMaterialDTO);
        //删除素材相关标签
        List<Long> materialList = Arrays.stream(removeMaterialDTO.getIds().split(","))
                .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        weMaterialTagRelService.delByMaterialId(materialList, removeMaterialDTO.getCorpId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWeMaterial(UpdateWeMaterialDTO weMaterial) {
        validWeMaterialParam(weMaterial, weMaterial.getMediaType());
        if (weMaterial.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //清理旧标签关联
        List<Long> materialList = new ArrayList<>();
        materialList.add(weMaterial.getId());
        weMaterialTagRelService.delByMaterialId(materialList, weMaterial.getCorpId());
        //重新打标签
        if (!CollectionUtils.isEmpty(weMaterial.getTagIdList())) {
            materialList.add(weMaterial.getId());
            weMaterialTagService.markTags(materialList, weMaterial.getTagIdList());
        }
        return weMaterialMapper.updateWeMaterial(weMaterial);
    }

    @Override
    public WeMaterial findWeMaterialById(Long id) {
        return weMaterialMapper.findWeMaterialById(id);
    }

    @Override
    public List<WeMaterialVO> findWeMaterials(FindWeMaterialDTO findWeMaterialDTO) {
        if (findWeMaterialDTO == null || findWeMaterialDTO.getIsExpire() == null || StringUtils.isBlank(findWeMaterialDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return weMaterialMapper.findWeMaterials(findWeMaterialDTO);
    }


    @Override
    public void resetCategory(String categoryId, String materials) {
        List<String> materialList = Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(materials, WeConstans.COMMA));
        if (CollUtil.isNotEmpty(materialList)) {
            for (String s : materialList) {
                weMaterialMapper.resetCategory(categoryId, s);
            }
        }
    }


    @Override
    public WeMediaDTO uploadTemporaryMaterial(String url, String type, String name, String corpId) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(type) || StringUtils.isBlank(name) || StringUtils.isBlank(corpId)) {
            throw new CustomException("请求参数不能为空");
        }
        int fileLength;
        try (
                InputStream inputStream = new URL(url).openConnection().getInputStream();
        ) {
            // 获取文件的长度
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            fileLength = bufferedInputStream.available();
            // 调用企微上传素材
            return weMediaClient.upload(inputStream, name, type, corpId, fileLength, WeConstans.WE_UPLOAD_FORM_DATA_CONTENT_TYPE);
        } catch (ForestRuntimeException e) {
            log.error("上传临时文件失败......url:{},type:{},name:{},ex:{},st:{}", url, type, name, ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
            WeResultDTO result = JSONUtil.toBean(e.getMessage(), WeResultDTO.class);
            if (WeExceptionTip.WE_EXCEPTION_TIP_40123.getCode().equals(result.getErrcode())) {
                throw new CustomException(ResultTip.TIP_IMAGE_FORMAT_ERROR);
            }
            throw new CustomException(ResultTip.TIP_GENERAL_ERROR);
        } catch (Exception e) {
            log.error("上传临时文件失败......url:{},type:{},name:{},ex:{},st:{}", url, type, name, ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
            throw new CustomException(ResultTip.TIP_GENERAL_ERROR);
        }
    }

    @Override
    public WeMediaDTO uploadAttachment(String url, String mediaType, Integer attachmentType, String name, String corpId) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(mediaType) || StringUtils.isBlank(name) || StringUtils.isBlank(corpId)) {
            throw new CustomException("请求参数不能为空");
        }
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            conn = null;
            URL materialUrl = new URL(url);
            conn = (HttpURLConnection) materialUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            inputStream = conn.getInputStream();
            return weMediaClient.uploadAttachment(inputStream, name, mediaType, attachmentType, corpId);
        } catch (IOException e) {
            log.error("附件朋友圈素材上传异常 corpId:{},url:{},e:{}", corpId, url, ExceptionUtils.getStackTrace(e));
        } finally {
            if (conn != null && inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("流关闭异常......ex:{},st:{}", ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
                }
            }
        }
        return null;
    }

    @Override
    public WeMediaDTO uploadImg(MultipartFile file, String corpId) {
        if (StringUtils.isBlank(corpId) || file == null) {
            throw new CustomException("请求参数不能为空");
        }
        return weMediaClient.uploadimg(file, corpId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void showMaterialSwitch(ShowMaterialSwitchDTO showMaterialSwitchDTO) {
        if (showMaterialSwitchDTO == null || StringUtils.isBlank(showMaterialSwitchDTO.getCorpId()) ||
                StringUtils.isBlank(showMaterialSwitchDTO.getIds()) || showMaterialSwitchDTO.getShowMaterial() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        weMaterialMapper.showMaterialSwitch(showMaterialSwitchDTO);
    }

    @Override
    @Transactional
    public void restore(RestoreMaterialDTO restoreMaterialDTO) {
        if (restoreMaterialDTO == null || StringUtils.isBlank(restoreMaterialDTO.getCorpId())
                || restoreMaterialDTO.getMediaType() == null || restoreMaterialDTO.getIds() == null || restoreMaterialDTO.getIds().length == 0) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        weMaterialMapper.restore(restoreMaterialDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMaterialByJob(String corpId, Date lastRemoveDate) {
        if (StringUtils.isBlank(corpId) || lastRemoveDate == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<Long> idList = weMaterialMapper.findExpireMaterialByLastExpireTime(corpId, lastRemoveDate);
        if (!CollectionUtils.isEmpty(idList)) {
            //删除过期素材
            int count = weMaterialMapper.deleteByIdListAndCorpId(corpId, idList);
            log.info("removeMaterialByJob corpId={},count={}", corpId, count);
            //删除标签关联
            weMaterialTagRelService.delByMaterialId(idList, corpId);
        }
    }

    /**
     * 查询素材数量、发布到侧边栏数量
     *
     * @param findWeMaterialDTO 筛选条件
     * @return {@link WeMaterialCountVO}
     */
    @Override
    public WeMaterialCountVO getMaterialCount(FindWeMaterialDTO findWeMaterialDTO) {
        //校验corpId
        com.easyink.common.utils.StringUtils.checkCorpId(findWeMaterialDTO.getCorpId());
        return weMaterialMapper.getMaterialCount(findWeMaterialDTO);
    }

    @Override
    public List<AddWeMaterialDTO> getListByMaterialSort(String[] materialSort, String corpId) {
        if (materialSort == null || materialSort.length == 0 || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return weMaterialMapper.getListByMaterialSort(materialSort, corpId);
    }

    @Override
    public List<AddWeMaterialDTO> getRedeemCodeListByMaterialSort(String[] codeSuccessMaterialSort, String corpId) {
        if (codeSuccessMaterialSort == null || codeSuccessMaterialSort.length == 0 || StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
        return weMaterialMapper.getListByMaterialSort(codeSuccessMaterialSort, corpId);
    }
}
