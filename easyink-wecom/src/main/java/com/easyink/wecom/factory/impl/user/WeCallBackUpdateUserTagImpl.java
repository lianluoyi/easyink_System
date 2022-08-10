package com.easyink.wecom.factory.impl.user;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeFlowerCustomerTagRel;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.WeFlowerCustomerTagRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 类名: 标签成员(员工)变更处理
 *
 * @author: 1*+
 * @date: 2021-09-24 14:08
 */
@Slf4j
@Component("update_tag")
public class WeCallBackUpdateUserTagImpl extends WeEventStrategy {
    @Autowired
    private WeFlowerCustomerTagRelService weFlowerCustomerTagRelService;
    @Autowired
    private WeFlowerCustomerRelService weFlowerCustomerRelService;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }
        if (StringUtils.isAnyBlank(message.getToUserName(), message.getTagId())) {
            log.error("标签更变事件失败：corpId:{},tagId:{}", message.getToUserName(), message.getTagId());
            return;
        }
        try {
            String tagId = message.getTagId();
            //标签中新增的成员userid列表，用逗号分隔
            List<String> addUserItemsList = Arrays.stream(Optional.ofNullable(message.getAddUserItems())
                    .orElse("").split(",")).collect(Collectors.toList());
            //标签中删除的成员userid列表，用逗号分隔
            List<String> delUserItemsList = Arrays.stream(Optional.ofNullable(message.getDelUserItems())
                    .orElse("").split(",")).collect(Collectors.toList());

            //标签中新增的成员userid列表，建立关联
            List<WeFlowerCustomerTagRel> weFlowerCustomerTagRels = new ArrayList<>();
            LambdaQueryWrapper<WeFlowerCustomerRel> relLambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (CollUtil.isNotEmpty(addUserItemsList)){
                relLambdaQueryWrapper.in(WeFlowerCustomerRel::getUserId, addUserItemsList);
            }
            relLambdaQueryWrapper.eq(WeFlowerCustomerRel::getCorpId, message.getToUserName());

            //查询客户关系
            List<WeFlowerCustomerRel> flowerCustomerRelList = weFlowerCustomerRelService.list(relLambdaQueryWrapper);
            //根据客户标签的id能取得标签关系的数据
            List<Long> idList = Optional.ofNullable(flowerCustomerRelList).orElseGet(ArrayList::new)
                    .stream().map(WeFlowerCustomerRel::getId).collect(Collectors.toList());
            idList.forEach(id -> weFlowerCustomerTagRels.add(WeFlowerCustomerTagRel.builder().flowerCustomerRelId(id)
                    .tagId(tagId)
                    .build()));
            weFlowerCustomerTagRelService.batchInsetWeFlowerCustomerTagRel(weFlowerCustomerTagRels);

            //当前标签对应成员列表
            LambdaQueryWrapper<WeFlowerCustomerTagRel> tagRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tagRelLambdaQueryWrapper.eq(WeFlowerCustomerTagRel::getTagId, tagId);
            List<WeFlowerCustomerTagRel> tagRelList = weFlowerCustomerTagRelService.list(tagRelLambdaQueryWrapper);

            List<Long> flowerCustomerRelIdList = Optional.ofNullable(tagRelList).orElseGet(ArrayList::new)
                    .stream().map(WeFlowerCustomerTagRel::getFlowerCustomerRelId).collect(Collectors.toList());

            if (!flowerCustomerRelIdList.isEmpty()) {
                LambdaQueryWrapper<WeFlowerCustomerRel> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(WeFlowerCustomerRel::getCorpId, message.getToUserName())
                        .in(WeFlowerCustomerRel::getId, flowerCustomerRelIdList)
                        .in(WeFlowerCustomerRel::getUserId, delUserItemsList);
                List<WeFlowerCustomerRel> relList = weFlowerCustomerRelService.list(queryWrapper);
                List<Long> relIdList = Optional.ofNullable(relList).orElseGet(ArrayList::new)
                        .stream().map(WeFlowerCustomerRel::getId).collect(Collectors.toList());
                //标签中删除的成员userid列表
                LambdaQueryWrapper<WeFlowerCustomerTagRel> tagRelQueryWrapper = new LambdaQueryWrapper<>();
                tagRelQueryWrapper.in(WeFlowerCustomerTagRel::getFlowerCustomerRelId, relIdList);
                weFlowerCustomerTagRelService.remove(tagRelQueryWrapper);
            }
        } catch (Exception e) {
            log.error("标签更变时间更新到本地失败：ex{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
