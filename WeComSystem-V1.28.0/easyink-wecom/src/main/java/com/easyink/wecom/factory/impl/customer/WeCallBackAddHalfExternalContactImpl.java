package com.easyink.wecom.factory.impl.customer;

import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author admin
 * @description 外部联系人免验证添加成员事件
 * @date 2021/1/20 23:28
 **/
@Slf4j
@Component("add_half_external_contact")
public class WeCallBackAddHalfExternalContactImpl extends WeEventStrategy {

    @Resource(name = "add_external_contact")
    private WeCallBackAddExternalContactImpl weCallBackAddExternalContact;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        // 与【新增客户事件】相同处理逻辑
        weCallBackAddExternalContact.eventHandle(message);
    }
}
