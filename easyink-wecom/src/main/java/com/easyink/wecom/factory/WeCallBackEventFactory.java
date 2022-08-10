package com.easyink.wecom.factory;

import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @description 企微回调工厂接口
 * @date 2020/11/9 17:12
 **/
@Service
public interface WeCallBackEventFactory {
    void eventHandle(WxCpXmlMessageVO message);
}
