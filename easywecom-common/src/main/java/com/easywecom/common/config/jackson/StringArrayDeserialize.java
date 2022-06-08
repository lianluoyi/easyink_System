package com.easywecom.common.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.easywecom.common.utils.StringUtils;

import java.io.IOException;

/**
 * @author admin
 * @Description:
 * @Date: create in 2020/9/15 0015 0:55
 */
public class StringArrayDeserialize extends JsonDeserializer<String[]> {
    @Override
    public String[] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        if (!StringUtils.isEmpty(jsonParser.getText())) {
            return jsonParser.getText().split(",");
        }

        return new String[0];
    }
}
