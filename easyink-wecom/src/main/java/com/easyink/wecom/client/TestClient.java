package com.easyink.wecom.client;

import com.dtflys.forest.annotation.Get;
import org.springframework.stereotype.Component;

/**
 * 类名:
 *
 * @author : silver_chariot
 * @date : 2023/3/9 19:13
 **/
@Component
public interface TestClient {

    @Get("cip.cc")
    String getIp();
}
