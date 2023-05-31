package com.clarolab.service;

import lombok.extern.java.Log;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.clarolab.util.Constants.LOG_CONSOLE_FILEPATH;

@Service
@Log
public class LogConsoleService {

    public String getLastLines(Integer cantLines) {
        String[] linesString;
        try (Stream<String> stream = Files.lines(Paths.get(LOG_CONSOLE_FILEPATH))) {
            StringBuilder lines = new StringBuilder();
            stream.forEach(line -> {
                if(line.startsWith("at")) line = "\t" + line;
                lines.append(line).append("\n");
            });
            linesString = lines.toString().split("\n");
        } catch (Exception e) {
            log.log(Level.SEVERE, String.format("Can't access to log file %s", LOG_CONSOLE_FILEPATH), e);
            return null;
        }
        if(cantLines == -1 || linesString.length - cantLines < 0){
            return String.join("\n", linesString);
        }
        return Arrays.stream(linesString).skip(linesString.length - cantLines).collect(Collectors.joining("\n"));
    }
}
