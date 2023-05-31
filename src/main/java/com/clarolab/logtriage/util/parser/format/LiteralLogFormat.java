package com.clarolab.logtriage.util.parser.format;

import com.clarolab.logtriage.util.parser.LogHeader;
import com.clarolab.util.Pair;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
public class LiteralLogFormat extends LogFormat {

    private String literal;

    public LiteralLogFormat(String literal) {
        super("literal");
        this.literal = literal.trim();
    }

    @Override
    public Pair<String, String> getNext(LogHeader line) {
        line.increment(this.literal.trim().split(line.getSeparator()).length-1);

        return resultPair(this.literal);
    }

    @Override
    public boolean isValid(String s) {
        return !StringUtils.isAlphanumeric(s);
    }

}
