package com.clarolab.connectors.impl.utils.report.cypress.json.deserializer;

import com.clarolab.connectors.impl.utils.report.cypress.json.entity.*;
import com.clarolab.util.JsonUtils;
import com.google.gson.*;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Type;
import java.util.List;

public class CypressJsonDeserializer implements JsonDeserializer<MainCypress> {

    @Override
    public MainCypress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        MainCypress mainCypress = MainCypress.builder().build();

        JsonObject statistic = JsonUtils.getObject(jsonElement, "stats");
        JsonElement result = JsonUtils.getAsArray(JsonUtils.getAsObject(jsonElement), "results");

        if (result == null) throw new AssertionError();
        result.getAsJsonArray().forEach(element -> mainCypress.addSuites(this.getCypressSuites(element.getAsJsonObject())));

        mainCypress.setStatistic(getStatistic(statistic));
        return mainCypress;
    }

    private CypressReportSummary getStatistic(JsonObject object) {
        return CypressReportSummary.builder()
                .suites(JsonUtils.parseInt(object, "suites"))
                .tests(JsonUtils.parseInt(object, "tests"))
                .passes(JsonUtils.parseInt(object, "passes"))
                .pending(JsonUtils.parseInt(object, "pending"))
                .failures(JsonUtils.parseInt(object, "failures"))
                .duration(JsonUtils.parseLong(object, "duration"))
                .passPercent(JsonUtils.parseLong(object, "passPercent"))
                .pendingPercent(JsonUtils.parseLong(object, "pendingPercent"))
                .testsRegistered(JsonUtils.parseInt(object, "testsRegistered"))
                .skipped(JsonUtils.parseInt(object, "skipped"))
                .build();
    }

    private List<CypressSuite> getCypressSuites(JsonObject jsonObject) {
        String suiteName = JsonUtils.parseString(jsonObject, "file");

        List<CypressSuite> suites = Lists.newArrayList();
        this.getCypressSuites(jsonObject, suites, suiteName);

        return suites;
    }

    private void getCypressSuites(JsonObject jsonObject, List<CypressSuite> suites, String suiteName) {
        JsonArray suitesArray = JsonUtils.getAsArray(jsonObject, "suites");
        JsonArray testsArray = JsonUtils.getAsArray(jsonObject, "tests");

        if (testsArray != null && testsArray.size() > 0) {
            CypressSuite cypressSuite = CypressSuite.builder()
                    .title((JsonUtils.parseString(jsonObject, "title")))
                    .uri(suiteName)
                    .testCases(this.getCypressTestCases(testsArray))
                    .build();

            suites.add(cypressSuite);

        }

        if (suitesArray != null && suitesArray.size() > 0) {
            suitesArray.forEach(suiteArray -> {
                JsonObject suiteObject = suiteArray.getAsJsonObject();
                this.getCypressSuites(suiteObject, suites, suiteName);
            });
        }
    }

    private List<CypressTestCase> getCypressTestCases(JsonArray elements) {
        List<CypressTestCase> testCases = Lists.newArrayList();
        elements.forEach(testCase -> {
            JsonObject object = testCase.getAsJsonObject();
            CypressTestCase cypressTestCase = CypressTestCase.builder()
                    .title(JsonUtils.parseString(object, "title"))
                    .duration(JsonUtils.parseLong(object, "duration"))
                    .status(JsonUtils.parseString(object, "state"))
                    .screenshotURL(this.getScreenshotURL(JsonUtils.getElement(object, "context")))
                    .videoURL(this.getVideoURL(JsonUtils.getElement(object, "context")))
                    .err(this.getError(JsonUtils.getAsObject(object, "err")))
                    .steps(this.getSteps(JsonUtils.getElement(object, "code")))
                    .build();

            testCases.add(cypressTestCase);
        });

        return testCases;
    }

    private CypressTestCaseError getError(JsonObject jsonObject) {
        if (jsonObject != null && !jsonObject.entrySet().isEmpty()) {
            return CypressTestCaseError.builder()
                    .message(JsonUtils.parseString(jsonObject, "message"))
                    .stack(JsonUtils.parseString(jsonObject, "estack"))
                    .build();
        }

        return null;
    }

    private String getScreenshotURL(JsonElement jsonElement) {
        if (!jsonElement.isJsonNull()) {
            String values = jsonElement.getAsString();
            if (values.contains(".png")) {
                values = values.replaceAll("\\n", "");
                values = values.replaceAll("\\[", "");
                values = values.replaceAll("]", "");
                values = values.replace("\"", "");
                values = values.trim();

                return values;
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
