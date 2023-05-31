/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class PerformaceUtil {

    private static Map<String, Long> times = new HashMap<>();

    public static void start(String key) {
        times.put(key, DateUtils.now());
    }

    public static long stop(String key) {
        Long timesOrDefault = times.getOrDefault(key, 0L);
        return TimeUnit.MILLISECONDS.toSeconds(DateUtils.now() - timesOrDefault);
    }

    public static String get(String key) {
        return String.format("%s Seconds", stop(key));
    }
}
