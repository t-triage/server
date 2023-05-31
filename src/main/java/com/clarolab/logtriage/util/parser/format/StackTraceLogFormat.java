package com.clarolab.logtriage.util.parser.format;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
public class StackTraceLogFormat extends LogFormat {

    private int index;
    private List<String> lines;
    private final Pattern pattern;

    public StackTraceLogFormat(List<String> lines, int index) {
        super("stackTrace");
        this.index = index;
        this.lines = lines;
        this.pattern = Pattern.compile(("((^\\tat )(.*)((\\((.*)\\))$))|^Caused by:(.*)"));
    }

    public List<String> getStackTrace() {
        List<String> stackTrace = new ArrayList<>();

        while (hasNext()) {
            if (isValid(getCurrentLine()) || (getIndex() < (getLines().size() - 1) && isValid(getLines().get(getIndex() + 1)))) {
                stackTrace.add(getCurrentLine());
                nextLine();
            } else {
                break;
            }
        }

        return stackTrace;
    }

    private String getCurrentLine() {
        return lines.get(index);
    }

    private void nextLine() {
        this.index++;
    }

    private boolean hasNext() {
        return index < lines.size();
    }

    @Override
    public boolean isValid(String s) {
        return pattern.matcher(s).matches();
    }
}
