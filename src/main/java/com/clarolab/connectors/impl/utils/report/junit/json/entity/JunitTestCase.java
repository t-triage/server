/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.model.types.StatusType;
import com.clarolab.util.StringUtils;
import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
public class JunitTestCase extends JunitBase{

    private String className;
    private JunitTestCaseError error;
    private String systemOut;
    private String systemErr;

    @Builder
    private JunitTestCase(String name, long time, String group, String className, JunitTestCaseError error, String systemOut, String systemErr){
        super(name, time, group);
        this.className = className;
        this.error = error;
        this.systemOut = systemOut;
        this.systemErr = systemErr;
    }

    public String getFullName(){
        return this.className+"."+this.getName();
    }

    public StatusType getStatus(){
        if(error != null){
            if(error.isSkipError())
                return StatusType.SKIP;
            else return StatusType.FAIL;
        }
        return StatusType.PASS;
    }

    public String getError(){
        return this.error != null ? this.error.getError() : StringUtils.getEmpty();
    }

    public String getErrorDetail(){
        StringBuilder out = new StringBuilder();
        String errorDetail = this.error != null ? this.error.getErrorDetail() : StringUtils.getEmpty();
        out.append(errorDetail);
        if(!Strings.isNullOrEmpty(this.systemOut)) {
            if(!out.toString().isEmpty())
                out.append(StringUtils.getLineSeparator());
            out.append(this.systemOut);
        }
        if(!Strings.isNullOrEmpty(this.systemErr)) {
            if(!out.toString().isEmpty())
                out.append(StringUtils.getLineSeparator());
            out.append(this.systemErr);
        }
        return removeSpecialChars(out.toString());
    }

    @Override
    public String toString(){
        return this.getFullName();
    }

    private String removeSpecialChars(String str){
        return str.replaceAll("&amp#\\d*;\\[(\\d*;)*\\d*m", "");
    }
}
