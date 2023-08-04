package com.high.shop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class WxMsg {
    @JsonProperty(value = "touser")
    private String toUser;

    @JsonProperty(value = "template_id")
    private String templateId;

    @JsonProperty(value = "url")
    private String url;

    @JsonProperty(value = "topcolor")
    private String topColor;

    @JsonProperty(value = "data")
    private Map<String, Map<String, String>> data;

    public static Map<String, String> buildData(String value, String color) {
        HashMap<String, String> map = new HashMap<>(16);
        map.put("value", value);
        map.put("color", color);
        return map;
    }

}
