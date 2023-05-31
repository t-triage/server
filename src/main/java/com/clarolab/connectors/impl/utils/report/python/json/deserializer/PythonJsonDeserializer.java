package com.clarolab.connectors.impl.utils.report.python.json.deserializer;
import com.clarolab.connectors.impl.utils.report.python.json.entity.*;
import com.clarolab.util.JsonUtils;
import com.google.gson.*;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Type;
import java.util.List;

public class PythonJsonDeserializer implements JsonDeserializer<MainPython> {
    double contador;
    @Override
    public MainPython deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        MainPython mainPython = MainPython.builder().build();

        JsonObject statistic = JsonUtils.getObject(jsonElement, "stats");
        JsonElement result = JsonUtils.getAsArray(JsonUtils.getAsObject(jsonElement), "test_cases");


        if (result == null) throw new AssertionError();

        List<PythonSuite> suites = Lists.newArrayList();
        addPythonSuites(suites, jsonElement, contador);
        mainPython.addSuites(suites);

        mainPython.setStatistic(getStatistic(statistic, jsonElement,contador));
        return mainPython;
    }


    private PythonReportSummary getStatistic(JsonObject object, JsonElement jsonElement,double contador) {
        JsonElement total = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "total");
        JsonElement passed = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "passed");
        JsonElement warning = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "warning");
        JsonElement failures = JsonUtils.getElement(JsonUtils.getAsObject(jsonElement), "failed");
        return PythonReportSummary.builder()
                .tests(collectStats(total))
                .passes(collectStats(passed))
                .warning(collectStats(warning))
                .failures(collectStats(failures))
                .duration(persistent(contador))
                .build();
    }
    private int collectStats(JsonElement jsonElement){
        return jsonElement.getAsInt();
    }
    private List<PythonSuite> addPythonSuites(List<PythonSuite> suites, JsonElement jsonElement, double contador) {
        JsonPrimitive suiteName = JsonUtils.getPrimitive((JsonObject) jsonElement, "suite");
        JsonArray testsArray = JsonUtils.getAsArray(JsonUtils.getAsObject(jsonElement), "test_cases");

        if (testsArray != null && testsArray.size() > 0) {

                PythonSuite pythonSuite = PythonSuite.builder()
                        .title(suiteName.getAsString())
                        .uri(suiteName.getAsString())
                        .testCases(this.getPythonTestCases(testsArray,contador))
                        .build();
                suites.add(pythonSuite);
        }
        return suites;
    }

    private List<PythonTestCase> getPythonTestCases(JsonArray elements,double contador) {
        List<PythonTestCase> testCases = Lists.newArrayList();
        elements.forEach(testCase -> {
            JsonObject object = testCase.getAsJsonObject();
            PythonTestCase pythonTestCase = PythonTestCase.builder()
                    .duration(JsonUtils.parseLong(object, "duration_ms"))
                    .status(JsonUtils.parseString(object, "status"))
                    .title(JsonUtils.parseString(object, "name"))
//                    .screenshotURL(JsonUtils.parseString(object, "status"))
//                    .videoURL(JsonUtils.parseString(object, "status"))
                    .err(JsonUtils.parseString(object, "exception_msg"))
                    .stackTrace(JsonUtils.parseString(object, "exception_stack_trace"))
//                    .steps(this.getSteps(JsonUtils.getElement(object, "exception_stack_trace")))
                    .build();
            testCases.add(pythonTestCase);
        });
        contador += testCases.stream().mapToInt(PythonTestCase -> (int) PythonTestCase.getDuration()).sum();
        persistent(contador);
        return testCases;
    }
    private double persistent(double a){
        contador+=a;
        return contador;
    }
    private PythonTestCaseError getError(JsonArray jsonArray) {
        if (jsonArray != null) {
            for(JsonElement j: jsonArray){
                return  PythonTestCaseError.builder()
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