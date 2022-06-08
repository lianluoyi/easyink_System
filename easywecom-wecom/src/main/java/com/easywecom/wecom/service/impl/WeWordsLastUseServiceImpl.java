package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeWordsLastUseEntity;
import com.easywecom.wecom.domain.vo.WeWordsVO;
import com.easywecom.wecom.mapper.WeWordsGroupMapper;
import com.easywecom.wecom.mapper.WeWordsLastUseMapper;
import com.easywecom.wecom.service.WeWordsLastUseService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名： 最近使用话术接口
 *
 * @author 佚名
 * @date 2021/11/3 9:51
 */
@Service
public class WeWordsLastUseServiceImpl extends ServiceImpl<WeWordsLastUseMapper, WeWordsLastUseEntity> implements WeWordsLastUseService {
    private static final int MAX_WORDS_SIZE = 5;
    private final WeWordsLastUseMapper weWordsLastUseMapper;
    private final WeWordsGroupMapper weWordsGroupMapper;

    @Autowired
    public WeWordsLastUseServiceImpl(WeWordsLastUseMapper weWordsLastUseMapper, WeWordsGroupMapper weWordsGroupMapper) {
        this.weWordsLastUseMapper = weWordsLastUseMapper;
        this.weWordsGroupMapper = weWordsGroupMapper;
    }

    @Override
    public void addOrUpdateLastUse(WeWordsLastUseEntity weWordsLastUseEntity) {
        WeWordsLastUseEntity lastUseEntity = weWordsLastUseMapper.getByUserId(weWordsLastUseEntity.getUserId(), weWordsLastUseEntity.getCorpId(), weWordsLastUseEntity.getType());
        String[] ids = weWordsLastUseEntity.getWordsIds();
        LinkedList<String> wordsIdList = new LinkedList<>();
        //更新链表
        if (lastUseEntity != null) {
            List<String> idList = Arrays.asList(lastUseEntity.getWordsIds());
            wordsIdList.addAll(idList);
            //链表里包含，将其放置第一位，前面的数据向后移一位
            if (wordsIdList.contains(ids[0])) {
                wordsIdList.remove(ids[0]);
            } else { //不包含
                if (MAX_WORDS_SIZE == wordsIdList.size()) {
                    //删除最后一个
                    wordsIdList.removeLast();
                }
            }
            //加入首位
            wordsIdList.addFirst(ids[0]);
            weWordsLastUseEntity.setWordsIds(wordsIdList.toArray(new String[]{}));
        }
        weWordsLastUseMapper.saveOrUpdate(weWordsLastUseEntity);
    }

    @Override
    public List<WeWordsVO> listOfWordsVO(String userId, String corpId, Integer type) {
        StringUtils.checkCorpId(corpId);
        WeWordsLastUseEntity lastUseEntity = weWordsLastUseMapper.getByUserId(userId, corpId, type);
        if (lastUseEntity == null) {
            return new ArrayList<>();
        }
        String[] wordsIds = lastUseEntity.getWordsIds();
        if (ArrayUtils.isEmpty(wordsIds)) {
            return new ArrayList<>();
        }
        List<Long> ids = Arrays.stream(wordsIds).map(Long::parseLong).collect(Collectors.toList());

        return weWordsGroupMapper.listOfWordsById(corpId, ids);
    }

}