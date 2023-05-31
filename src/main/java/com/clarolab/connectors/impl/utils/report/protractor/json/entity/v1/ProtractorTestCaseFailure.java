package com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1;

import com.clarolab.util.StringUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProtractorTestCaseFailure {

    private String type;
    private String message;
    private String content;

    public String getError(){
        if(noErrorPresent())
            return null;
        return String.format("Error type [%s : '%s']", type, message);
    }

    public String getDetailedError(){
        if(noErrorPresent())
            return null;
        return String.format("Error type [%s : '%s'] \n\n\n %s", type, message, content);
    }

    public boolean noErrorPresent(){
        return StringUtils.isEmpty(type) || StringUtils.isEmpty(message) || StringUtils.isEmpty(content);
    }

}
