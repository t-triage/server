package com.clarolab.logtriage.util.parser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
public class LogHeader {

    private String separator;
    private int currentPosition;
    private String[] line;

    public LogHeader(String[] line, int currentPosition, String separator) {
        this.currentPosition = currentPosition;
        this.line = line;
        this.separator = separator;
    }

    public LogHeader(String[] line, String separator) {
        this.currentPosition = 0;
        this.line = line;
        this.separator = separator;
    }

    public LogHeader(String line, String separator) {
        this.currentPosition = 0;
        this.separator = separator;
        this.line = line.split(separator);
    }

    public int increment() {
        return this.increment(1);
    }

    public int increment(int sum) {
        setCurrentPosition(getCurrentPosition() + sum);
        return getCurrentPosition();
    }

    public String getCurrentContent() {
        if (line.length <= currentPosition)
            return line[0];
        return line[currentPosition];
    }

    public boolean hasNext() {
        return line.length > currentPosition;
    }

}
