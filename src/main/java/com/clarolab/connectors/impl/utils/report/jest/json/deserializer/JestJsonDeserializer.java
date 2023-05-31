package com.clarolab.connectors.impl.utils.report.jest.json.deserializer;
import com.clarolab.connectors.impl.utils.report.jest.json.entity.*;
import com.clarolab.util.JsonUtils;
import com.google.gson.*;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JestJsonDeserializer implements JsonDeserializer<MainJest> {
    double contador;
    @Override
    public MainJest deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        MainJest mainJest = MainJest.builder().build();

        JsonObject statistic = JsonUtils.getObject(jsonElement, "stats");
        JsonElement result = JsonUtils.getAsArray(JsonUtils.getAsObject(jsonElement), "testResults");

        if (result == null) throw new AssertionError();
        result.getAsJsonArray().forEach(element -> mainJest.addSuites(this.getJestSuites(element,contador)));

        mainJest.setStatistic(getStatistic(statistic, jsonElement,contador));
        return mainJest;
    }

    private JestReportsSummary getStatistic(JsonObject object, JsonElement jsonElement,double contador) {
        JsonElement suites = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "numTotalTestSuites");
        JsonElement numTotalTests = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "numTotalTests");
        JsonElement numPassedTests = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "numPassedTests");
        JsonElement numPendingTests = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "numPendingTests");
        JsonElement numFailedTests = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "numFailedTests");
        JsonElement skipped = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "numTodoTests");
        return JestReportsSummary.builder()
                .suites(collectStats(suites))
                .numTotalTests(collectStats(numTotalTests))
                .numPassedTests(collectStats(numPassedTests))
                .numPendingTests(collectStats(numPendingTests))
                .numFailedTests(collectStats(numFailedTests))
                .duration(persistent(contador))
                .skipped(collectStats(skipped))
                .build();
    }
    private int collectStats(JsonElement jsonElement){
        return jsonElement.getAsInt();
    }

    private List<JestSuite> getJestSuites(JsonElement jsonElement, double contador) {
        JsonPrimitive suiteName = JsonUtils.getPrimitive((JsonObject) jsonElement, "testFilePath");

        List<JestSuite> suites = Lists.newArrayList();
        this.getJestSuites(jsonElement, suites, suiteName.getAsString(),contador);

        return suites;
    }

    private void getJestSuites(JsonElement jsonElement, List<JestSuite> suites, String suiteName,double contador) {
        JsonArray testsArray = JsonUtils.getAsArray(jsonElement.getAsJsonObject(), "testResults");
        if (testsArray != null && testsArray.size() > 0) {

            JestSuite jestSuite = JestSuite.builder()
                    .title((JsonUtils.parseString(jsonElement.getAsJsonObject(), "title")))
                    .uri(suiteName)
                    .testCases(this.getJestTestCases(testsArray,contador))
                    .build();

            suites.add(jestSuite);

        }

        if (testsArray != null && testsArray.size() > 0) {
            testsArray.forEach(suiteArray -> {
                JsonObject suiteObject = suiteArray.getAsJsonObject();
                this.getJestSuites(suiteObject, suites, suiteName,contador);
            });
        }

    }

    private List<JestTestCase> getJestTestCases(JsonArray elements,double contador) {
        List<JestTestCase> testCases = Lists.newArrayList();
        AtomicInteger aux= new AtomicInteger();
        elements.forEach(testCase -> {
            aux.addAndGet(1);
            JsonObject object = testCase.getAsJsonObject();
            String[] shortTitle;
            String title = JsonUtils.parseString(object,"title");
            title = title + " #"+aux;
            if(title.contains("/")){
                shortTitle = title.split("/");
                title = shortTitle[shortTitle.length-1];
                shortTitle= title.split(".stories");
                title= shortTitle[0];
                title = title + " #"+aux;

                ;}
            JestTestCase jestTestCase = JestTestCase.builder()
                    .duration(JsonUtils.parseLong(object, "duration"))
                    .status(JsonUtils.parseString(object, "status"))
                    .title(title)
//                    .screenshotURL(JsonUtils.parseString(object, "status"))
//                    .videoURL(JsonUtils.parseString(object, "status"))
                    .err(this.getError(JsonUtils.getAsArray(object, "failureMessages")))
                    .steps(this.getSteps(JsonUtils.getElement(object, "numPassingAsserts")))
                    .build();
            testCases.add(jestTestCase);
        });
        contador += testCases.stream().mapToInt(jestTestCase -> (int) jestTestCase.getDuration()).sum();
        persistent(contador);
        return testCases;
    }
    private double persistent(double a){
        contador+=a;
        return contador;
    }
    private JestTestCaseError getError(JsonArray jsonArray) {
        if (jsonArray != null) {
            for(JsonElement j: jsonArray){
                return  JestTestCaseError.builder()
                        .message(JsonUtils.getAsString(j).replaceAll("\\u001b","").replaceAll("31m","").replaceAll("39m","").replaceAll("36m","").replaceAll("90m",""))
                        .build();
            }
        }
        return null;
    }

    private String getScreenshotURL(JsonElement jsonElement) {
        if (!jsonElement.isJsonNull()) {
            String[] values = jsonElement.getAsString().split(",");
            for (String value : values) {
                if (value.contains(".png")) {
                    value = value.replaceAll("\\n", "");
                    value = value.replaceAll("\\[", "");
                    value = value.replaceAll("]", "");
                    value = value.replace("\"", "");
                    value = value.trim();

                    return value;
                }
            }
        }

        return null;
    }

    private String getVideoURL(JsonElement jsonElement) {
        if (!jsonElement.isJsonNull()) {
            String[] values = jsonElement.getAsString().split(",");
            for (String value : values) {
                if (value.contains(".mp4")) {
                    value = value.replaceAll("\\n", "");
                    value = value.replaceAll("\\[", "");
                    value = value.replaceAll("]", "");
                    value = value.replace("\"", "");
                    value = value.trim();

                    return value;
                }
            }
        }

        return null;
    }

    private List<String> getSteps(JsonElement jsonElement) {
        List<String> steps = Lists.newArrayList();

        if (!jsonElement.isJsonNull()) {
            String[] values = jsonElement.getAsString().split("cy\\.");

            for (String value : values) {
                value = value.replaceAll("(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)", ""); // To remove multi-line comments
                if (value.contains("http") && !value.startsWith("//")) {
                    value = value.replaceAll("[^:]//.*|/\\*((?!=*/)(?s:.))+\\*/", ""); // To remove one-line comments
                } else {
                    value = value.replaceAll("(/\\*((.|\n)*?)\\*/)|//.*", ""); // To remove one-line comments
                }

                if (value.trim().length() > 1) {
                    value = "cy." + value.trim();
                    steps.add(value);
                }
            }

            return steps;
        }

        return null;
    }

}