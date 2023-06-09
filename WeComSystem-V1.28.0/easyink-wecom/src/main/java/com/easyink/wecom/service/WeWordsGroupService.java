package com.easyink.wecom.service;

import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.WeWordsGroupEntity;
import com.easyink.wecom.domain.dto.WeWordsDTO;
import com.easyink.wecom.domain.dto.WeWordsQueryDTO;
import com.easyink.wecom.domain.dto.WeWordsSortDTO;
import com.easyink.wecom.domain.vo.WeWordsImportVO;
import com.easyink.wecom.domain.vo.WeWordsUrlVO;
import com.easyink.wecom.domain.vo.WeWordsVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 类名： 话术库主表接口
 *
 * @author 佚名
 * @date 2021/10/25 17:37
 */
public interface WeWordsGroupService {
    /**
     * 添加
     *
     * @param weWordsDTO 实体DTO
     */
    void add(WeWordsDTO weWordsDTO);

    /**
     * 查询
     *
     * @param id 主键
     * @return {@link WeWordsGroupEntity}
     */
    WeWordsGroupEntity get(Integer id);

    /**
     * 更新
     *
     * @param weWordsDTO 实体
     */
    void update(WeWordsDTO weWordsDTO);

    /**
     * 删除话术
     *
     * @param ids 主键
     */
    void delete(List<Long> ids, String corpId);

    /**
     * 根据目录id删除
     *
     * @param categoryIds id
     */
    void deleteByCategoryId(List<Long> categoryIds, String corpId);

    /**
     * 查询话术库
     *
     * @param weWordsQueryDTO 话术dto
     * @return {@link List<WeWordsVO>}
     */
    List<WeWordsVO> listOfWords(WeWordsQueryDTO weWordsQueryDTO);

    /**
     * 批量修改话术文件夹
     *
     * @param categoryId 文件夹id
     * @param ids        话术id
     * @param corpId     企业id
     */
    void updateCategory(Long categoryId, List<Long> ids, String corpId);

    /**
     * 导入话术
     *
     * @param file      文件
     * @param loginUser 登录用户
     * @param type      话术类型
     * @return {@link WeWordsImportVO}
     */
    WeWordsImportVO importWords(MultipartFile file, LoginUser loginUser, Integer type);

    /**
     * 链接内的内容
     *
     * @param address 链接
     * @return {@link WeWordsUrlVO}
     */
    WeWordsUrlVO matchUrl(String address);

    /**
     * 文件夹上移/下移/置顶
     *
     * @param sortDTO
     * @return int
     */
    int changeSort(WeWordsSortDTO sortDTO);
}

