/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.test;

import com.clarolab.unit.BaseUnitTest;
import com.clarolab.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsUnitTest extends BaseUnitTest {

    @Test
    public void method1() {
        String text = "TestBadgeAchieveCount{Dimension}[1]";
        String shouldBe = "test Badge Achieve Count";

        String result = StringUtils.methodToWords(text);

        Assert.assertEquals(shouldBe, result);
    }

    @Test
    public void method2() {
        String text = "testABC{Dimension}[1](4)";
        String shouldBe = "test ABC";

        String result = StringUtils.methodToWords(text);

        Assert.assertEquals(shouldBe, result);
    }

    @Test
    public void method3() {
        String text = "com.clarolab.test.PersonTestClass{Dimension}[1](4)";
        String shouldBe = "Person test Class";

        String result = StringUtils.methodToWords(text);

        Assert.assertEquals(shouldBe, result);
    }


    @Test
    public void params1() {
        String text = "TestBadgeAchieveCount{Dimension}[1]";
        String[] shouldBe = {"Dimension", "1"};

        String[] result = StringUtils.getParameters(text);

        int i = 0;
        for (String expected : shouldBe) {
            Assert.assertEquals(expected, result[i]);
            i++;
        }
    }

    @Test
    public void params2() {
        String text = "testABC{Dimension}[1](4)";
        String[] shouldBe = {"Dimension", "1", "4"};

        String[] result = StringUtils.getParameters(text);

        int i = 0;
        for (String expected : shouldBe) {
            Assert.assertEquals(expected, result[i]);
            i++;
        }
    }

    @Test
    public void apiValidateSQLParams() {
        String text = "validateSQLLogs(lithium-cloud-auto-int-pri2-h-single.dynjcint.jivehosted.com, SELECT)";
        String[] shouldBe = {"lithium-cloud-auto-int-pri2-h-single.dynjcint.jivehosted.com, SELECT"};

        String[] result = StringUtils.getParameters(text);

        int i = 0;
        for (String expected : shouldBe) {
            Assert.assertEquals(expected, result[i]);
            i++;
        }
    }

    @Test
    public void apiValidateSQLName() {
        String text = "validateSQLLogs(lithium-cloud-auto-int-pri2-h-single.dynjcint.jivehosted.com, SELECT)";
        String shouldBe = "Validate SQL Logs";

        String result = StringUtils.methodToWords(text);

        Assert.assertEquals(shouldBe, result);
    }

    @Test
    public void robotValidateName() {
        String text = "Test Common Visible i18n Keys on Video";
        String shouldBe = "test Common Visible i18n Keys on Video";

        String result = StringUtils.methodToWords(text);

        Assert.assertEquals(shouldBe, result);
    }

    @Test
    public void i18nValidateName() {
        String text = "i18n míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com";
        String shouldBe = "i18n Míñç Usernew/míñç Üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com";

        String result = StringUtils.methodToWords(text);

        Assert.assertEquals(shouldBe, result);
    }
}
