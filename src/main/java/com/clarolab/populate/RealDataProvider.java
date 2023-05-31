/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;


import com.clarolab.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.TEST_DATA_PATH;

/**
 * This class provides several real names and type of content
 * Reads data from resources/sample*
 */

@Component
@Log
public class RealDataProvider {
    List<TestTriagePopulate> tests = getTests();
    List<String> containers = getContainers();
    List<String> executors = getExecutors();
    List<String> users = getUserRealNames();


    public static void main(String[] args) {
        RealDataProvider instance = new RealDataProvider();

        System.out.println("Containers");
        for (String text : instance.getContainers()) {
            System.out.println(instance.getContainer());
        }
        System.out.println();

        System.out.println("Executors");
        for (String text : instance.getExecutors()) {
            System.out.println(instance.getExecutor());
        }
        System.out.println();

        System.out.println("User Real Names");
        for (String name : instance.getUserRealNames()) {
            System.out.println(instance.getUserRealName());
        }
        System.out.println();

        System.out.println("TestTriagePopulate");
        for (TestTriagePopulate test : instance.getTests()) {
            System.out.println(instance.getTest().getTestCaseName());
        }
        System.out.println();

        System.out.println("TestNames");
        for (TestTriagePopulate test : instance.getTests()) {
            System.out.println(StringUtils.methodToWords(test.getTestCaseName()));
        }

        System.out.println();
    }

    private List<String> getContainers() {
        try {
            containers = readLines(TEST_DATA_PATH + "sample_containers.txt");
            return containers;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error accessing sample data: sample_containers.txt", e);
        }
        return null;
    }

    private List<String> getExecutors() {
        try {
            executors = readLines(TEST_DATA_PATH + "sample_executors.txt");
            return executors;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error accessing sample data: sample_executors.txt", e);
        }
        return null;
    }

    private List<String> getUserRealNames() {
        try {
            users = readLines(TEST_DATA_PATH + "sample_users.txt");
            return users;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error accessing sample data: sample_users.txt", e);
        }
        return null;
    }

    public List<TestTriagePopulate> getAllTests() {
        return tests;
    }

    public List<TestTriagePopulate> getTests(String path) {
        try {
            tests = readJson(TEST_DATA_PATH + path);
            return tests;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error accessing sample data: sample_tests.txt", e);
        }
        return null;
    }

    private List<TestTriagePopulate> getTests() {
        return getTests("sample_tests.txt");
    }

    public String getContainer() {
        int pos =  (int) ((Math.random() * containers.size()) -1);
        return containers.get(pos);
    }

    public String getExecutor() {
        int pos =  (int) ((Math.random() * executors.size()) -1);
        return executors.get(pos);
    }

    public String getUserRealName() {
        int pos =  (int) ((Math.random() * users.size()) -1);
        return users.get(pos);
    }

    public TestTriagePopulate getBasicTest() {
        int pos =  (int) ((Math.random() * tests.size()) -1);
        return getTest(pos);
    }

    public TestTriagePopulate getTest() {
        TestTriagePopulate test = getBasicTest();
        try {
            test = test.clone();
        } catch (CloneNotSupportedException e) {
            log.log(Level.SEVERE, "Error cloning test");
        }
        test.setTestCaseName(DataProvider.getRandomName(test.getTestCaseName(), 2));

        return test;
    }

    public TestTriagePopulate getTest(int pos) {
        int index = pos % tests.size();
        return tests.get(index);
    }

    /**
     * JSON got by
     *
     * SELECT CONCAT('{',
     *  '"testCaseName": "',t.name, '", ',
     *  '"clazz": "',t.locationPath, '", ',
     *  '"suiteName": "',e.suiteName, '", ',
     *  '"errorDetails": "',REPLACE(e.errorDetails, '"', '-'), '", ',
     *  '"errorStackTrace": "',REPLACE(e.errorStackTrace, '"', '-'), '" ',
     *  '},'
     *  ) FROM QA_TEST_EXECUTION e, QA_Test_Case t
     *  where errorDetails != '' AND e.testCase_id = t.id
     *  LIMIT 100
     *
     * @param jasonfile
     * @return
     */

    public List<TestTriagePopulate> readJason(String jasonfile) {
        Type listType = new TypeToken<List<TestTriagePopulate>>() {}.getType();
        Gson gsonRead = new Gson();
        Reader reader = null;
        try {
            reader = new FileReader(jasonfile);
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Error reading file: " + jasonfile, e);
        }
        List<TestTriagePopulate> out = gsonRead.fromJson(reader, listType);
        return out;
    }

    public List<String> readFile(String filename) {
        List<String> answer = new ArrayList<>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));

        try {
            String line = br.readLine();

            while (line != null) {
                answer.add(line);
                line = br.readLine();
            }
        } finally {
            br.close();
        }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error reading file: " + filename, e);
        }

        return answer;
    }

    private List<String> readLines(String resourceName) {
        InputStream inputStream = RealDataProvider.class.getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            return Collections.emptyList();
        } else {
            return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());
        }
    }

    private List<TestTriagePopulate> readJson(String resourceName) {
        Type listType = new TypeToken<List<TestTriagePopulate>>() {}.getType();
        Gson gsonRead = new Gson();
        return gsonRead.fromJson(String.join("", this.readLines(resourceName)), listType);
    }
}
