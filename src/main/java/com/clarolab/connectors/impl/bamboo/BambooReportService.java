package com.clarolab.connectors.impl.bamboo;

import com.clarolab.bamboo.entities.BambooArtifact;
import com.clarolab.connectors.impl.utils.report.AppVersion;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.CVSLog;
import com.clarolab.model.Report;
import com.clarolab.model.types.LogType;
import com.clarolab.service.CVSRepositoryService;
import com.clarolab.util.JsonUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.http.auth.AuthenticationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class BambooReportService {

    private ReportUtils reportUtils;
    private ApplicationContextService context;

    @Builder
    private BambooReportService(ApplicationContextService context){
        reportUtils = ReportUtils.builder().context(context).build();
        this.context = context;
    }

    public Report getReport(List<BambooArtifact> bambooArtifacts, String observations){
        reportUtils.setApplicationTestingEnvironmentVersion(getApplicationTestingEnvironmentVersionFromArtifacts(bambooArtifacts));
        List<Report> reports = Lists.newArrayList();

        Optional<BambooArtifact> versionArtifact = bambooArtifacts.stream().filter(artifact -> isVersion(artifact)).findFirst();
        String productVersion = null;

        if (versionArtifact.isPresent()) {
            try {
                productVersion = AppVersion.getVersion(versionArtifact.get().getContent());
            } catch (URISyntaxException | IOException | AuthenticationException e) {
                log.log(Level.WARNING, "Error getting version from: " + versionArtifact.get().getResourceLink(), e);
            }
        }
        
        bambooArtifacts.stream()
                .filter(artifact -> !(artifact.isLog() || artifact.isImage() || isVersion(artifact)) && !LogType.isCVSLog(artifact.getName()))
                .forEach(artifact -> reports.add(getReport(artifact, observations)));

        getCVSLogs(bambooArtifacts.stream()
                .filter(artifact -> LogType.isCVSLog(artifact.getName()))
                .collect(Collectors.toList()), observations);

        List<Report> reportList = reports.stream().filter(report -> report != null).collect(Collectors.toList());

        if (!StringUtils.isEmpty(productVersion)) {
            reportUtils.setApplicationTestingEnvironmentVersion(productVersion);
            String finalProductVersion = productVersion;
            reportList.stream().forEach(report -> report.setProductVersion(finalProductVersion));
        }
        return reportUtils.getReport(reportList);
    }

    private Report getReport(BambooArtifact bambooArtifact, String observations){
        try {
            if(bambooArtifact.isADirectory()) {
                List<Report> reports = Lists.newArrayList();
                for (Map.Entry<String, String> entry : bambooArtifact.getContentAtDirectory().entrySet()) {
                    Report newReport = reportUtils.createReport(entry.getValue(), reportUtils.getReportTypeFromFileName(entry.getKey()), observations);
                    if (newReport != null) {
                        reports.add(newReport);
                    }
                }
                return reportUtils.getReport(reports.stream().filter(r -> r != null).collect(Collectors.toList()));
            } else {
                return reportUtils.createReport(bambooArtifact.getContent(), reportUtils.getReportTypeFromFileName(bambooArtifact.getResourceName()), observations);
            }

        } catch (URISyntaxException | IOException | AuthenticationException e) {
            log.log(Level.SEVERE, "[getReport]: There was an error trying to get report for file " + bambooArtifact.getName(), e);
            return null;
        }
    }

    public List<CVSLog> getCVSLogs(List<BambooArtifact> cvsArtifacts, String observations) {
        try {
            if (cvsArtifacts == null || cvsArtifacts.isEmpty()) {
                return null;
            }
            List<CVSLog> cvsLogs = new ArrayList<>();
            String fileContent;
            CVSRepositoryService service = context.getCvsRepositoryService();

            if (context.getProduct() != null && !StringUtils.isEmpty(context.getProduct().getPackageNames())) {
                for(BambooArtifact artifact: cvsArtifacts) {
                    fileContent = artifact.getContent();

                    cvsLogs.addAll(service.readAndProcess(fileContent, context.getProduct(), LogType.getLogType(artifact.getName())));
                }
            } else {
                log.log(Level.WARNING, "[getCVSLogs] There are CVS logs but product packages are not configured");
            }

            return cvsLogs;

        } catch (Exception e) {
            log.log(Level.SEVERE, "[getCVSLogs] Error getting CVS logs from artifact", e);
            return null;
        }
    }


    private String getApplicationTestingEnvironmentVersionFromArtifacts(List<BambooArtifact> artifacts) {
        BambooArtifact artifact = artifacts.stream().filter(e -> e.getName().matches("(\\W|\\w)*(V|v)ersion\\.json")).findFirst().orElse(null);
        String content = StringUtils.getEmpty();
        try {
            content = artifact != null ? artifact.getContent() : StringUtils.getEmpty();
        } catch (URISyntaxException | IOException | AuthenticationException e) {
            log.log(Level.WARNING, "There was an error trying to get artifact " + artifact.getLink().getHref() + ".", e);
        }
        return  JsonUtils.getApplicationVersionFromJson(content);
    }
    
    private boolean isVersion(BambooArtifact artifact) {
        if (artifact == null) {
            return false;
        }
        return AppVersion.isVersionURL(artifact.getResourceLink());
    }

}
