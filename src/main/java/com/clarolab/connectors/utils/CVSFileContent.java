package com.clarolab.connectors.utils;

import com.clarolab.model.types.LogType;
import com.clarolab.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CVSFileContent {

    public static boolean isLog(String url) {
        return !StringUtils.isEmpty(url) && (url.contains(".log") || LogType.isCVSLog(url));
    }

    public static List<String> getFileContent(String url) {
        if (!isLog(url))
            return null;

        List<String> fileContent = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
            while (bufferedReader.ready())
                fileContent.add(bufferedReader.readLine());
        } catch (IOException e) {
            System.out.println("Couldn't read file: " + url);
            e.printStackTrace();
        }

        return fileContent;
    }
}
