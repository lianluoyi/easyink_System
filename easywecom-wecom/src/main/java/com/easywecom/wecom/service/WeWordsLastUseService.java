package com.easywecom.wecom.service;

import com.easywecom.wecom.domain.WeWordsLastUseEntity;
import com.easywecom.wecom.domain.vo.WeWordsVO;

import java.util.List;

/**
 * 类名： 最近使用话术表接口
 *
 * @author 佚名
 * @date 2021-11-02 10:32:00
 */
public interface WeWordsLastUseService {
    /**
     * 添加或更新
     *
     * @param weWordsLastUseEntity 最近使用话术
     */
    void addOrUpdateLastUse(WeWordsLastUseEntity weWordsLastUseEntity);


    /**
     * 获取最近使用话术（包含附件）
     *
     * @param userId 员工id（admin 传‘admin’）
     * @param corpId 企业id
     * @param type   话术类型
     * @return {@link List<WeWordsVO>}
     */
    List<WeWordsVO> listOfWordsVO(String userId, String corpId, Integer type);
}

