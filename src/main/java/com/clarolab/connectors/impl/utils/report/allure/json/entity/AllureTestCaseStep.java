package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import com.clarolab.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class AllureTestCaseStep {

    private String name;
    private String status;
    private List<AllureTestCaseStep> steps;
    private List<AllureTestCaseStepParameter> parameters;

    public String getParameters(){
        StringBuilder parameter = new StringBuilder();
        if(CollectionUtils.isNotEmpty(parameters)) {
            for (AllureTestCaseStepParameter p : parameters) {
                parameter.append(p.getName());
                parameter.append(":");
                parameter.append(p.getValue());
                parameter.append("; ");
            }
            parameter.deleteCharAt(parameter.length()-1);
            parameter.deleteCharAt(parameter.length()-1);//Remove last one ; char
        }
        return parameter.toString();
    }

    public String getOutput(){
        return getFullOutput(1);
    }

    private String getFullOutput(int depthTab){
        StringBuilder out = new StringBuilder(name);
        if(!StringUtils.isEmpty(getParameters()))
            out.append("[" + getParameters()+ "]");
        out.append("\n");

        for(AllureTestCaseStep step: steps){
            out = StringUtils.addTab(out, depthTab);
            out.append(step.getFullOutput(depthTab+1));
        }

        return out.toString();
    }



}
