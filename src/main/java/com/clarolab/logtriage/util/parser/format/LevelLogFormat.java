package com.clarolab.logtriage.util.parser.format;

import java.util.Arrays;

public class LevelLogFormat extends LogFormat {

    String[] levels = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"};

    public LevelLogFormat() {
        super("level");
    }

    @Override
    public boolean isValid(String s) {
        return Arrays.stream(levels).anyMatch(s::contains);
    }
}
