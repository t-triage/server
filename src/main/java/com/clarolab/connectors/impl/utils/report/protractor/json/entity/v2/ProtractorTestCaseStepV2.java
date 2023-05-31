package com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2;

import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.ProtractorTestCaseFailure;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProtractorTestCaseStepV2 {

    private String classname;
    private String name;
    private String time;
    private String screenshotURL;
    private ProtractorTestCaseFailure failure;

    public boolean isFailedStep(){
        return failure != null && !failure.noErrorPresent();
    }

    public String getError(){
        return failure.getError();
    }

    public String getDetailedError(){
        return failure.getDetailedError();
    }

    public String getOutput(){
        return getDetailedError();
    }

    private String getTesCaseName(){
        if (classname.contains(">")) {
            return classname.substring(classname.indexOf(">")+1).trim();
        } else {
            return classname;
        }
    }

    public String getTesCaseStepName(){
        if (classname == null) {
            return name;
        }
        String suite = classname;
        if (classname.contains(".")) {
            suite = classname.substring(classname.indexOf(".") + 1);
        }
        if (name.contains(suite)) {
            return name.substring(name.indexOf(suite) + suite.length() + 1).trim();
        }
        return name.replaceAll("(\\w|\\W)*"+getTesCaseName(), "").trim();
    }
}
