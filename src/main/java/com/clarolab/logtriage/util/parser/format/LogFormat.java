package com.clarolab.logtriage.util.parser.format;

import com.clarolab.logtriage.util.parser.LogHeader;
import com.clarolab.util.Pair;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LogFormat {

    public final String fieldName;

    public LogFormat(String fieldName) {
        this.fieldName = fieldName;
    }

    public Pair<String, String> getNext(LogHeader line) {
        String content = line.getCurrentContent();
        line.increment();
        return resultPair(content);
    }

    protected Pair<String, String> resultPair(String result) {
        return new Pair<>(getFieldName(), result);
    }

    public abstract boolean isValid(String s);

}
