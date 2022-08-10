package com.easyink.common.service.impl;

import com.easyink.common.core.domain.entity.SysDictData;
import com.easyink.common.mapper.SysDictDataMapper;
import com.easyink.common.service.ISysDictDataService;
import com.easyink.common.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author admin
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }


    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    @Override
    public int deleteDictDataByIds(Long[] dictCodes) {
        int row = dictDataMapper.deleteDictDataByIds(dictCodes);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData dictData) {
        int row = dictDataMapper.insertDictData(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData dictData) {
        int row = dictDataMapper.updateDictData(dictData);
        if (row > 0) {
            DictUtils.clearDictCache();
        }
        return row;
    }
}
