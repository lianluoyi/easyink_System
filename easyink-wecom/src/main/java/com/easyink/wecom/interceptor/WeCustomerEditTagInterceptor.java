package com.easyink.wecom.interceptor;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.ForestResponse;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DictUtils;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeFlowerCustomerTagRel;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.dto.customer.CustomerTagEdit;
import com.easyink.wecom.domain.dto.customer.req.EditTagReq;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.WeFlowerCustomerTagRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 编辑客户标签拦截器
 *
 * @author lichaoyu
 * @date 2023/6/20 14:11
 */
@Slf4j
@Component
public class WeCustomerEditTagInterceptor extends WeAccessTokenInterceptor {


    private final WeFlowerCustomerTagRelService weFlowerCustomerTagRelService;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    /**
     * Header中的企业ID标识
     */
    private final static String CORP_ID = "corpid";
    /**
     * 非法标签返回响应的errorMsg中invalid tagid文案位置
     */
    private final int ERR_INVALID_MSG_NUM = 0;
    /**
     * 非法标签返回响应，截取标签ID内容的文案位置
     */
    private final int ERR_INVALID_MSG_SPLIT_TAG_ID_NUM = 1;
    /**
     * 非法标签返回响应的标签ID位置
     */
    private final int ERR_TAG_ID_NUM = 0;
    /**
     * 非法标签返回响应内容截取标识
     */
    private final String SPILT_ERR_MSG = ":";
    /**
     * 操作频繁状态码
     */
    private static final  Integer OPR_FREQ_ERR_CODE = 45033 ;

    private final String BODY_ADD_TAG = "add_tag";

    private final String BODY_EXTERNAL_USERID = "external_userid";

    private final String BODY_USERID = "userid";

    private final String BODY_REMOVE_TAG = "remove_tag";

    private final int BODY_EXR_USERID_NUM = 0;

    @Lazy
    public WeCustomerEditTagInterceptor(WeFlowerCustomerTagRelService weFlowerCustomerTagRelService, WeFlowerCustomerRelService weFlowerCustomerRelService) {
        this.weFlowerCustomerTagRelService = weFlowerCustomerTagRelService;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
    }

    /**
     * 请求成功调用(微信端错误异常统一处理)
     *
     * @param o
     * @param forestRequest
     * @param forestResponse
     */
    @Override
    public void onSuccess(Object o, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.info("url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());
        WeResultDTO weResultDto = JSONUtil.toBean(forestResponse.getContent(), WeResultDTO.class);
        // 如果返回的状态码是84061，表示该客户与员工没有客户关系，抛出异常，不进行标签重试。
        if (null != weResultDto.getErrcode() && !WeConstans.WE_SUCCESS_CODE.equals(weResultDto.getErrcode()) && WeConstans.NOT_EXIST_CONTACT.equals(weResultDto.getErrcode())) {
            throw new ForestRuntimeException(forestResponse.getContent());
        }
        // 非法标签重试
        retryMarkTag(forestRequest, forestResponse);
    }

    /**
     * 非法标签重试
     *
     * @param forestRequest
     * @param forestResponse
     */
    private void retryMarkTag(ForestRequest forestRequest, ForestResponse forestResponse) {
        WeResultDTO weResultDTO = JSONUtil.toBean(forestResponse.getContent(), WeResultDTO.class);
        if( weResultDTO != null && OPR_FREQ_ERR_CODE.equals(weResultDTO.getErrcode())) {
            throw new CustomException(WeExceptionTip.WE_EXCEPTION_TIP_45033);
        }
        String corpId = forestRequest.getHeaderValue(CORP_ID);
        List<String> notExistsTagIdList = new ArrayList<>();
        /*
           企微返回的非法标签消息内容格式：result:{"errcode":40068,"errmsg":"invalid tagid: et39zyBwAA02DegHo-ykxyLf41canggA, hint: [1687318430480130194986936],....}
            当errcode = 40068，errMsg开头为 invalid tagid，表示该编辑标签请求存在非法的标签，通过":"截取errMsg中第一个字符串内容，为invalid tagid表示是非法标签的响应，进行处理
         */
        if (null != weResultDTO.getErrcode() && WeConstans.WE_ERROR_TAG_NO_EXISTS.equals(weResultDTO.getErrcode())
                && WeConstans.WE_TAG_NO_EXISTS_ERR_MSG.equals(weResultDTO.getErrmsg().split(SPILT_ERR_MSG)[ERR_INVALID_MSG_NUM])) {
            // 将errMsg里非法的标签存入列表，通过":"先截取errMsg中的invalid tagid和后边包含标签ID的内容，再通过","截取第一个字符串内容，就是标签ID
            notExistsTagIdList.add(weResultDTO.getErrmsg().split(SPILT_ERR_MSG)[ERR_INVALID_MSG_SPLIT_TAG_ID_NUM].split(DictUtils.SEPARATOR)[ERR_TAG_ID_NUM]
                    .replace(WeConstans.WE_TAG_NO_EXISTS_ERR_MSG, "").trim());
        }
        if (CollectionUtils.isNotEmpty(notExistsTagIdList)) {
            // TODO: 该处理方法为临时解决方法，待后续修改到调用请求时处理重试请求 Tower 任务: 活码标签执行异常 ( https://tower.im/teams/636204/todos/70486 )
            // 获取请求body参数
            CustomerTagEdit customerTagEdit = getForestBodyValue(forestRequest);
            List<String> originAddTagList = new ArrayList<>(Arrays.asList(customerTagEdit.getAdd_tag()));
            // 删除不存在的标签
            removeTag(originAddTagList, notExistsTagIdList, customerTagEdit, corpId);
            log.info("[编辑客户标签] 存在非法的标签，正在发起重试请求，本次尝试添加的标签:{}, 非法的标签：{}, corpId:{}", customerTagEdit.getAdd_tag(), notExistsTagIdList, corpId);
            // 重新发起请求
            resetBodyValue(forestRequest, customerTagEdit);
            forestRequest.execute();
        }
    }

    /**
     * 重新设置Forest请求参数
     *
     * @param forestRequest {@link ForestRequest}
     * @param customerTagEdit {@link CustomerTagEdit}
     */
    private void resetBodyValue(ForestRequest forestRequest, CustomerTagEdit customerTagEdit) {
        // 清除原来的请求中不合法的参数
        forestRequest.getBody().clear();
        // 设置新的参数
        forestRequest.addBody(BODY_ADD_TAG, customerTagEdit.getAdd_tag());
        forestRequest.addBody(BODY_REMOVE_TAG, customerTagEdit.getRemove_tag());
        forestRequest.addBody(BODY_USERID, customerTagEdit.getUserid());
        forestRequest.addBody(BODY_EXTERNAL_USERID, customerTagEdit.getExternal_userid());
    }

    /**
     * 获取BODY参数
     *
     * @param forestRequest {@link ForestRequest}
     * @return {@link CustomerTagEdit}
     */
    private CustomerTagEdit getForestBodyValue(ForestRequest forestRequest) {
        CustomerTagEdit customerTagEdit = new CustomerTagEdit();
        List<ForestRequestBody> forestRequestBody = forestRequest.getBody();
        for (ForestRequestBody requestBody : forestRequestBody) {
            EditTagReq editTagReq = JSONUtil.toBean(JSON.toJSONString(requestBody), EditTagReq.class);
            if (BODY_ADD_TAG.equals(editTagReq.getName())) {
                customerTagEdit.setAdd_tag(editTagReq.getValue());
            }
            if (BODY_REMOVE_TAG.equals(editTagReq.getName())) {
                customerTagEdit.setRemove_tag(editTagReq.getValue());
            }
            if (BODY_USERID.equals(editTagReq.getName())) {
                customerTagEdit.setUserid(editTagReq.getValue()[BODY_EXR_USERID_NUM]);
            }
            if (BODY_EXTERNAL_USERID.equals(editTagReq.getName())) {
                customerTagEdit.setExternal_userid(editTagReq.getValue()[BODY_EXR_USERID_NUM]);
            }
        }
        return customerTagEdit;
    }

    /**
     * 移除不存在的标签
     *
     * @param originAddTagList 原始的标签
     * @param notExistsTagIdList 不存在的标签
     */
    private void removeTag(List<String> originAddTagList, List<String> notExistsTagIdList, CustomerTagEdit customerTagEdit, String corpId) {
        Iterator<String> iterator = originAddTagList.iterator();
        while (iterator.hasNext()) {
            String tagId = iterator.next();
            for (String s : notExistsTagIdList) {
                if (s.equals(tagId)) {
                    iterator.remove();
                }
            }
        }
        customerTagEdit.setAdd_tag(originAddTagList.toArray(new String[0]));
        // 查询查出员工和客户关系
        WeFlowerCustomerRel flowerCustomerRel = weFlowerCustomerRelService.getOne(customerTagEdit.getUserid(), customerTagEdit.getExternal_userid(), corpId);
        if (flowerCustomerRel == null) {
            return;
        }
        // 删除之前本地添加的标签关系
        weFlowerCustomerTagRelService.remove(new LambdaQueryWrapper<WeFlowerCustomerTagRel>()
                .eq(WeFlowerCustomerTagRel::getFlowerCustomerRelId, flowerCustomerRel.getId())
                .eq(WeFlowerCustomerTagRel::getExternalUserid, flowerCustomerRel.getExternalUserid())
                .in(WeFlowerCustomerTagRel::getTagId, notExistsTagIdList));
    }
}
