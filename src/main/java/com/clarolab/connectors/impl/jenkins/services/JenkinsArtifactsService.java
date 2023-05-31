package com.clarolab.connectors.impl.jenkins.services;

import com.clarolab.model.Artifact;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.ArtifactType;
import com.clarolab.util.StringUtils;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Builder
public class JenkinsArtifactsService {

    public String getImageArtifact(TestExecution test, List<Artifact> artifacts){
        return getArtifactForTestCase(test, artifacts, ArtifactType.IMAGE);
    }

    public String getVideoArtifact(TestExecution test, List<Artifact> artifacts){
        return getArtifactForTestCase(test, artifacts, ArtifactType.VIDEO);
    }

    private String getArtifactForTestCase(TestExecution test, List<Artifact> artifacts, ArtifactType artifactType){
        if(artifactType.equals(ArtifactType.IMAGE)){
            for(Artifact imageArtifact: artifacts.stream().filter(artifact -> artifact.getArtifactType().equals(ArtifactType.IMAGE)).collect(Collectors.toList())){
                //find screenshot in suite and name
                if(artifactMatchWithSuiteAndName(imageArtifact, getShortSuiteName(test), test.getName()) ||
                //find screenshot in errorStackTrace
                   artifactMatchWithErrorStackTrace(imageArtifact, test.getErrorStackTrace()) ||
                   artifactMatchWithTestCaseScreenshotSource(imageArtifact, test.getFirstScreenshot())) {
                    return imageArtifact.getUrl();
                }
            }
        }

        if(artifactType.equals(ArtifactType.VIDEO)){
            //TODO
        }

        return StringUtils.getEmpty();
    }

    private String getShortSuiteName(TestExecution test){
        String[] elements;
        if(test.getSuiteName().contains("."))
            elements = test.getSuiteName().split("\\.");
        else if(test.getSuiteName().contains("/"))
            elements = test.getSuiteName().split("/");
        else return test.getSuiteName();

        return elements[elements.length-1];
    }

    private boolean artifactMatchWithSuiteAndName(Artifact artifact, String suiteName, String testName){
        return artifact.getName().contains(suiteName) && artifact.getName().contains(testName);
    }

    private boolean artifactMatchWithErrorStackTrace(Artifact artifact, String errorStacktrace){
        if(StringUtils.isEmpty(errorStacktrace))
            return false;
        return errorStacktrace.contains(artifact.getName());
    }

    private boolean artifactMatchWithTestCaseScreenshotSource(Artifact artifact, String screenshotSource){
        //For Allure report, screenshot name is saved on screenshotURL parameter. See MainAllure#102
        return !StringUtils.isEmpty(screenshotSource) && artifact.getName().equals(screenshotSource);
    }
}
