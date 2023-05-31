package com.clarolab.unit.test;

import com.clarolab.unit.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ComplexDataProviderTest extends BaseUnitTest {

    @Parameterized.Parameters(name = "{0}; {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { DataProviderElement.builder().attribute1("Element 1").attribute2("Element 2").build(), 1},
                { DataProviderElement.builder().attribute1("Element 3").attribute2("Element 4").build(), 2},
                { DataProviderElement.builder().attribute1("Element 5").attribute2("Element 6").build(), 3},
                { DataProviderElement.builder().attribute1("Element 7").attribute2("Element 8").build(), 4},
                { DataProviderElement.builder().attribute1("Element 9").attribute2("Element 10").build(), 5},
                { DataProviderElement.builder().attribute1("Element 11").attribute2("Element 12").build(), 6}
        });
        //Report return  simpleDataProviderOnReportTest[DataProviderElement(attribute1=Element 1, attribute2=Element 2); 1]
    }

    public ComplexDataProviderTest(DataProviderElement dataProviderElement, int number){
        this.dataProviderElement = dataProviderElement;
        this.number = number;
    }

    private DataProviderElement dataProviderElement;
    private int number;

    @Test
    public void simpleDataProviderOnReportTest(){
        Assert.assertTrue(this.dataProviderElement.getAttribute1().startsWith("Element") && this.number > 0);
    }
}
