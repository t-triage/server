package com.clarolab.connectors.impl.jenkins.report;

import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.entities.JenkinsBuild;

public abstract class JenkinsBaseReport {

    protected JenkinsBuild jenkinsBuild;
    protected ApplicationContextService context;
    protected String applicationTestingEnvironmentVersion;
    protected String cvsLogs;

}
