package com.easywecom.wecom.interceptor;

import cn.hutool.core.util.RandomUtil;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.NameValueRequestBody;
import com.dtflys.forest.interceptor.Interceptor;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.utils.sign.Md5Utils;
import com.easywecom.common.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author admin
 * @description: 微信token拦截器
 * @create: 2020-08-27 22:36
 **/
@Slf4j
@Component
public class OrderInterceptor implements Interceptor<Object> {


    private String accessId;
    private String accessKey;
    private final ForestConfiguration forestConfiguration;
    private final String orderServerUrl;


    @Lazy
    public OrderInterceptor() {
        this.forestConfiguration = SpringUtils.getBean(ForestConfiguration.class);
        accessId = String.valueOf(forestConfiguration.getVariableValue(WeConstans.ORDER_ACCESS_ID));
        accessKey = String.valueOf(forestConfiguration.getVariableValue(WeConstans.ORDER_ACCESS_KEY));
        orderServerUrl = String.valueOf(forestConfiguration.getVariableValue(WeConstans.ORDER_SERVER_URL));
    }


    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {

        String accessTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String accessNonce = RandomUtil.randomString(6);
        String accessSign = Md5Utils.hash(accessKey + Md5Utils.hash(accessNonce + accessTimestamp));

        request.addHeader("access-id", accessId);
        request.addHeader("access-nonce", accessNonce);
        request.addHeader("access-timestamp", accessTimestamp);
        request.addHeader("access-sign", accessSign);
        try {
            genUrl(request);
        } catch (Exception e) {
            log.error("[访问工单系统]替换url异常,{},{}", request, ExceptionUtils.getStackTrace(e));
        }
        return true;
    }


    /**
     * 访问路径后面拼接网点ID,格式： http://121.37.253.126:8088/{网点ID}
     *
     * @param request Forest请求
     */
    private void genUrl(ForestRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getBody()) || request.getUrl() == null) {
            return;
        }
        List<ForestRequestBody> list = request.getBody();
        String uri = request.getUrl().replace(String.valueOf(orderServerUrl), "");
        for (ForestRequestBody body : list) {
            NameValueRequestBody nameValueRequestBody = (NameValueRequestBody) body;
            // 非networkId不处理
            if (!GenConstants.NETWORK_ID.equals(nameValueRequestBody.getName())) {
                continue;
            }
            // 访问路径后需要拼接网点ID
            String url = orderServerUrl + "/" + nameValueRequestBody.getValue();
            request.setUrl(url + uri);
            break;
        }
    }


    /**
     * 请求发送失败时被调用
     *
     * @param e
     * @param forestRequest
     * @param forestResponse
     */
    @Override
    public void onError(ForestRuntimeException e, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.error("请求失败url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());
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

    }


}
