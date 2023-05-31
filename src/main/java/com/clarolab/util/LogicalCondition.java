/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.util;

import java.util.Arrays;
import java.util.function.Predicate;

public class LogicalCondition {

    public static boolean OR(Boolean... expressions) {
        return Arrays.asList(expressions).contains(true);
    }

    public static boolean AND(Boolean... expressions) {
        return Arrays.stream(expressions).allMatch(e -> e.equals(true));
    }

    public static boolean NOT(Boolean expression) {
        return !expression;
    }


    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
