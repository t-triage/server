package com.clarolab.logtriage.util.parser.format;

import java.util.regex.Pattern;

public class ClassLogFormat extends LogFormat {

    private final Pattern pattern;

    public ClassLogFormat() {
        super("class");
        this.pattern = Pattern.compile("([\\w]*\\.[\\w]+)");
    }

    @Override
    public boolean isValid(String s) {
        return pattern.matcher(s).find();
    }
}
