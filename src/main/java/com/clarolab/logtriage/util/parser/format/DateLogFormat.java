package com.clarolab.logtriage.util.parser.format;

import com.clarolab.logtriage.util.parser.LogHeader;
import com.clarolab.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringJoiner;

@Getter
@Setter
@Log
public class DateLogFormat extends LogFormat {

    // yyyy-MM-dd HH:mm:ss,SSSS
    private String type;

    public DateLogFormat(String type) {
        super("date");
        this.type = type;
    }

    @Override
    public Pair<String, String> getNext(LogHeader line) {
        String[] dateFormat = getType().split(line.getSeparator());

        StringJoiner joiner = new StringJoiner(line.getSeparator());
        for (int x = 0; line.hasNext(); x++) {
            if (x >= dateFormat.length)
                break;
            joiner.add(line.getCurrentContent());
            line.increment();
        }

        DateFormat sdf = new SimpleDateFormat(getType());
        sdf.setLenient(false);
        try {
            return resultPair(Long.toString(sdf.parse(joiner.toString()).getTime()));
        } catch (ParseException e) {
            return resultPair(null);
        }
    }

    @Override
    public boolean isValid(String s) {
        return NumberUtils.isDigits(s);
    }

}
