package com.clarolab.controller.impl;

import com.clarolab.controller.LogConsoleController;
import com.clarolab.service.LogConsoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static com.clarolab.util.Constants.LOG_CONSOLE_FILEPATH;

@CrossOrigin
@RestController

public class LogConsoleControllerImpl implements LogConsoleController {
    @Autowired
    LogConsoleService logConsoleService;

    @Override
    public ResponseEntity<String> getLogs(Integer cantLines) {
        return ResponseEntity.ok(logConsoleService.getLastLines(cantLines));
    }

    @Override
    public ResponseEntity<Resource> getLogsFile() throws IOException {

        File downloadFile = new File(Paths.get(LOG_CONSOLE_FILEPATH).toUri());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(downloadFile.toPath()));
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"LOGS - %s\"", new Date()));
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(downloadFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
