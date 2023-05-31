package com.clarolab.logtriage.util.parser.format;

import com.clarolab.logtriage.util.parser.LogHeader;
import com.clarolab.util.Pair;

import java.util.StringJoiner;

public class MessageLogFormat extends LogFormat {

    public MessageLogFormat() {
        super("message");
    }

    @Override
    public Pair<String, String> getNext(LogHeader line) {
        StringJoiner joiner = new StringJoiner(line.getSeparator());

        while (line.hasNext()) {
            joiner.add(line.getCurrentContent());
            line.increment();
        }

        return resultPair(joiner.toString());
    }

    @Override
    public boolean isValid(String s) {
        return true;
    }

}
