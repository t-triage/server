package com.clarolab.connectors.impl.utils.report.testng.json.entity;

import com.clarolab.util.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
public class TestNGSuiteTestClassTestMethodParameter {

    private int position;
    private String value;

    @Builder
    private TestNGSuiteTestClassTestMethodParameter(JsonElement element){
        JsonObject obj = JsonUtils.getAsObject(element);
        this.position = JsonUtils.parseInt(obj, "-index");
        if(JsonUtils.isJsonObject(obj, "value")){
            this.value = cleanFormat(Boolean.toString(JsonUtils.parseBoolean(JsonUtils.getAsObject( JsonUtils.getElement(obj, "value")), "is-null")));
        }else
            this.value = cleanFormat(JsonUtils.parseString(obj, "value"));
    }

    private String cleanFormat(String str){
        return str.replaceAll("\n", "").replaceAll("#", "").trim();
    }
}
