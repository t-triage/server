package com.clarolab.connectors.impl.utils.report;

import com.clarolab.util.JsonUtils;
import com.clarolab.util.StringUtils;
import org.json.JSONObject;

public class AppVersion {
    
    public static boolean isVersionURL(String url) {
        return StringUtils.isEmpty(url) ? false : url.contains("version.json");
    }
    
    public static String getVersion(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        String content = StringUtils.cleanup(text);
        String detectedVersion = null;
        
        if (JsonUtils.isJson(content) && content.contains("ersion")) {
            JSONObject jsonObj = new JSONObject(content);
            if (jsonObj == null) {
                return null;
            }
            if (jsonObj.has("version") && !StringUtils.isEmpty(jsonObj.getString("version"))) {
                detectedVersion = jsonObj.getString("version");
            }
            if (jsonObj.has("appVersion") && !StringUtils.isEmpty(jsonObj.getString("appVersion"))) {
                detectedVersion = jsonObj.getString("appVersion");
            }
        } else {
            if (content.length() < 400) {
                // assuming the URL contains the version
                detectedVersion = content;
            }
        }
        if (StringUtils.isEmpty(detectedVersion)) {
            return null;
        } else {
            return StringUtils.truncateStringAtLong(StringUtils.cleanup(detectedVersion), 255);
        }
    }
}
