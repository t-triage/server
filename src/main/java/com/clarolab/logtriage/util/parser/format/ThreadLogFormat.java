package com.clarolab.logtriage.util.parser.format;

public class ThreadLogFormat extends LogFormat {

    public ThreadLogFormat() {
        super("thread");
    }

    @Override
    public boolean isValid(String s) {
        return true;
    }
}
