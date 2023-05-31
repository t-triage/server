/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteKeywordType;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteMessageType;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.util.StringUtils;
import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Data
@Builder
@Log
public class SuiteReportKeywordData {

    private String name;
    private String doc;
    private SuiteKeywordType type;
    private SuiteReportArguments arguments;
    private SuiteStatusReport status;
    private List<SuiteReportKeywordData> kw;
    private List<SuiteReportMessageElement> msg;


    public boolean hasFailure() {
        return this.getStatus().isFailure();
    }

    public String getFail() {
        if (CollectionUtils.isNotEmpty(kw)) {
            return this.getAllKeywords().map(SuiteReportKeywordData::getFailDetail).filter(value -> !Strings.isNullOrEmpty(value)).findFirst().get();
        }
        else
            return getFailDetail();
    }

    // TODO: Implement
    public TestExecutionStep getStep(){
        return TestExecutionStep.builder()
                .name(this.name)
                .parameters(getParameters(arguments))
                .output(getFullOutput(1))
                .build();
    }

    private String getParameters(SuiteReportArguments arguments){
        StringBuilder args = new StringBuilder();
        if(arguments == null)
            return args.toString();

        arguments.getArg().forEach(arg -> args.append(arg).append(","));
        //Remove last character ","
        return args.deleteCharAt(args.length()-1).toString();
    }

    private String getFullOutput(int depthTab){
        StringBuilder out = new StringBuilder();
        out.append(name);
        String params = getParameters(arguments);
        if(!StringUtils.isEmpty(params))
            out.append(": ");
            out.append(params);
        out.append("\n");

        for(SuiteReportMessageElement output: msg){
            out = StringUtils.addTab(out, depthTab);
            out.append(output.getContent()).append("\n");
        }

        if(!CollectionUtils.isEmpty(kw)){
            for(SuiteReportKeywordData k: kw){
                out = StringUtils.addTab(out, depthTab);
                out.append(k.getFullOutput(depthTab+1));
            };
        }

        return out.toString();
    }

    private Stream<SuiteReportKeywordData> getAllKeywords() {
        return Stream.concat(Stream.of(this), kw.stream().flatMap(SuiteReportKeywordData::getAllKeywords));
    }

    private String getFailDetail() {
        AtomicInteger position = new AtomicInteger();

        SuiteReportMessageElement fail = msg.stream()
                .peek(x -> position.incrementAndGet())
                .filter(m -> m.getLevel() != null && m.getLevel().equals(SuiteMessageType.FAIL))
                .findFirst()
                .orElse(null);

        if (fail == null)
            return StringUtils.getEmpty();

        StringBuffer str = new StringBuffer();
        //This error is level FAIL
        str.append(fail.getContent());
        try {
            //This error is level DEBUG with Stacktrace
            String debugMessage = msg.get(position.get()).getContent();
            str.append(StringUtils.getLineSeparator());
            str.append(debugMessage);
        } catch (IndexOutOfBoundsException e) {
            log.severe("There was an error trying to get DEBUG level message after a FAIL level. Usually caused due do a test timeout.");
        }

        return str.toString();
    }

}
