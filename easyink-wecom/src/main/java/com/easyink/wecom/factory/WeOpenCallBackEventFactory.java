package com.easyink.wecom.factory;

import com.easyink.wecom.domain.vo.WeOpenXmlMessageVO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import org.springframework.stereotype.Service;

/**
 * 微信开放平台回调事件工厂接口
 *
 * @author wx
 * 2023/1/12 18:32
 **/
@Service
public interface WeOpenCallBackEventFactory {
    void eventHandle(WeOpenXmlMessageVO message);

}
