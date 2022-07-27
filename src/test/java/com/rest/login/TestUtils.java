package com.rest.login;

import io.restassured.path.json.JsonPath;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    static public Long getClientId(JsonPath json) {
        return json.getLong("client.id");
    }

    static public Long getClientLongParameter(JsonPath json, String parameter) {
        return json.getLong("client."+parameter);
    }

    static public String getClientParameter(JsonPath json, String parameter) {
        return json.getString("client."+parameter);
    }
}
