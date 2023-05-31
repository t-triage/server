/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.util;

import com.clarolab.connectors.impl.utils.report.AppVersion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonUtils {

    public static boolean parseBoolean(JsonObject object, String field){
        return parseBoolean(object, field, false);
    }

    public static boolean parseBoolean(JsonObject object, String field, boolean defaultValue) {
        if (object==null || object.get(field) == null|| object.get(field).isJsonNull()) {
            return defaultValue;
        } else {
            return object.get(field).getAsBoolean();
        }
    }

    public static long parseLong(JsonObject object, String field){
        return parseLong(object, field, 0L);
    }

    public static long parseLong(JsonObject object, String field, long defaultValue) {
        if (object==null || object.get(field) == null|| object.get(field).isJsonNull()) {
            return defaultValue;
        } else {
            return object.get(field).getAsLong();
        }
    }

    public static int parseInt(JsonElement element, String field){
        return parseInt(element.getAsJsonObject(), field, 0);
    }

    public static int parseInt(JsonObject object, String field){
        return parseInt(object, field, 0);
    }

    public static int parseInt(JsonObject object, String field, int defaultValue) {
        if (object==null || object.get(field) == null|| object.get(field).isJsonNull()) {
            return defaultValue;
        } else {
            return object.get(field).getAsInt();
        }
    }

    public static String parseString(JsonObject object, String field) {
        return parseString(object, field, StringUtils.getEmpty());
    }

    public static String parseString(JsonObject object, String field, String defaultValue) {
        if (object==null || object.get(field) == null || object.get(field).isJsonNull()) {
            return defaultValue;
        } else {
            return object.get(field).getAsString();
        }
    }

    public static String parseObjectAsString(JsonObject object, String objectField, String field, String defaultValue) {
        JsonElement obj = object.get(objectField);
        if (obj == null) {
            return defaultValue;
        } else {
            JsonObject newObject = obj.getAsJsonObject();
            return parseString(newObject, field, defaultValue);
        }
    }

    public static JsonObject getAsObject(JsonObject obj, String field){
        return getAsObject(getElement(obj, field));
    }

    public static JsonObject getAsObject(JsonElement element){
        return element == null ? null : element.getAsJsonObject();
    }

    public static JsonObject getObject(JsonElement element, String field){
        JsonElement obj = getAsObject(element).get(field);
        if(obj != null)
            return getAsObject(getAsObject(element).get(field));
        return null;
    }

    public static JsonElement getElement(JsonObject object, String field){
        return object==null ? null : object.get(field);
    }
    public static JsonPrimitive getPrimitive(JsonObject object, String field){
        return object==null ? null : (JsonPrimitive) object.get(field);
    }
    public static JsonArray getAsArray(JsonObject object, String field){
        JsonElement obj = object.getAsJsonObject().get(field);
        if(obj != null)
            return object.getAsJsonObject().get(field).getAsJsonArray();
        return null;
    }

    public static JsonArray getAsArray(JsonElement element){
        if (element == null) {
            return null;
        }
        return element.getAsJsonArray();
    }

    public static boolean isJsonArray(JsonElement element){
        return element != null && element.isJsonArray();
    }

    public static boolean isJsonObject(JsonObject obj, String field){
        return JsonUtils.getElement(obj, field).isJsonObject();
    }

    public static boolean isJsonObject(JsonElement element){
        if (element == null) {
            return false;
        }
        return element.isJsonObject();
    }

    public static String getAsString(JsonElement element){
        return element.getAsString();
    }

    public static String getApplicationVersionFromJson(String fileContent) {
        return AppVersion.getVersion(fileContent);
    }
    
    public static boolean isJson(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        
        return text.startsWith("{") && text.endsWith("}");
    }

}
