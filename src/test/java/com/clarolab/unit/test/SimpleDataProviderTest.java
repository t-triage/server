package com.clarolab.unit.test;

import com.clarolab.unit.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SimpleDataProviderTest extends BaseUnitTest {

    @Parameters(name = "{0}; {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "parameter1", "parameter1"},
                { "parameter2", "parameter1"},
                { "parameter3", "parameter1"},
                { "parameter4", "parameter1"},
                { "parameter5", "parameter1"},
                { "parameter6", "parameter1"}
        });
        //Report return simpleDataProviderOnReportTest[parameter1; parameter1]
    }

    public SimpleDataProviderTest(String parameter1, String parameter2){
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    private String parameter1;
    private String parameter2;

    @Test
    public void simpleDataProviderOnReportTest(){
        Assert.assertTrue(this.parameter1.startsWith("parameter"));
    }
}
