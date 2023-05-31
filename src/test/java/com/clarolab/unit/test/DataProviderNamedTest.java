/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.test;

import com.clarolab.unit.BaseUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

// The goal is just to validate that we can later read this kind of tests successfully from the application
@RunWith(Parameterized.class)
public class DataProviderNamedTest extends BaseUnitTest {

    @Parameters(name = "sort{index}: sort[{0}]={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "abc", "abc"},
                { "cba", "abc"},
                { "abcddcba", "aabbccdd"},
                { "a", "a"},
                { "aaa", "aaa"},
                { "", ""}
        });
    }

    private final String input;
    private final String expected;

    public DataProviderNamedTest(final String input, final String expected){
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testSort(){
        assertEquals(expected, sort(input));
    }

    private static String sort(final String s) {
        final char[] charArray = s.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }
}
