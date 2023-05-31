/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum LogType {
    GIT(1),
    SVN(2),
    MERCURIAL(3);

    private final int logType;

    LogType(int logType) {
        this.logType = logType;
    }

    public int getLogType() {
        return this.logType;
    }

    public static LogType getLogType(String artifact) {
        if (artifact == null)
            return null;
        if (isGitLog(artifact))
            return LogType.GIT;
        if (isSVNlog(artifact))
            return LogType.SVN;
        if (isMercurialLog(artifact))
            return LogType.MERCURIAL;
        return null;
    }

    public static boolean isCVSLog(String artifact) {
        return isGitLog(artifact) || isSVNlog(artifact) || isMercurialLog(artifact);
    }

    public static boolean isGitLog(String artifact) {
        if (artifact == null)
            return false;
        if (artifact.matches("(\\W|\\w)*(L|l)og\\.git")) {
            return true;
        }

        return (artifact.contains("Git") ||artifact.contains("git")) && (artifact.contains("Log") ||artifact.contains("log"));
    }

    public static boolean isSVNlog(String artifact) {
        if (artifact == null)
            return false;
        return (artifact.contains("svn") ||artifact.contains("SVN")) && (artifact.contains("Log") ||artifact.contains("log"));
    }

    public static boolean isMercurialLog(String artifact) {
        if (artifact == null)
            return false;
        return (artifact.contains("hg") ||artifact.contains("HG")) && (artifact.contains("Log") ||artifact.contains("log"));
    }
}
