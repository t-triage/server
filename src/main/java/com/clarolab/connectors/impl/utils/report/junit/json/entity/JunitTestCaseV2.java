package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.entities.JenkinsTestCase;
import com.clarolab.util.StringUtils;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
public class JunitTestCaseV2 extends JenkinsTestCase {

    private String stderr;

    @Override
    public String getErrorStackTrace(){
        if(StringUtils.isEmpty(super.getErrorStackTrace()))
            return StringUtils.getEmpty();

        StringBuilder error = new StringBuilder(super.getErrorStackTrace());
        if(!StringUtils.isEmpty(stderr))
            error.append(StringUtils.getLineSeparator()).append(stderr);
        return error.toString();
    }
}
