package com.clarolab.unit.test;

import com.clarolab.unit.BaseUnitTest;
import org.junit.Test;

public class StatusOptionGeneratorTest extends BaseUnitTest {

    private String[] actualStatuses = {"Fail", "Pass", "Skip", "Abort", "Fixed"};
    private String[] prevStatuses = {"Fail", "Pass", "Permanent", "NewFail", "Skip", "Invalid"};
    private String[] prevProducts = {"Works", "External", "Issue", "WontFix", "Skip"};
    private String[] sameErrors = {"Yes", "No"};


    @Test
    public void testSort(){
        for (String actualStatus : actualStatuses) {
            for (String prevStatus : prevStatuses) {
                for (String prevProduct : prevProducts) {
                    for (String prevAutom : prevProducts) {
                        for (String sameError : sameErrors) {
                            System.out.println(String.format("%s, %s, %s, %s, %s", actualStatus, prevStatus, prevProduct, prevAutom, sameError));
                        }
                    }
                }
            }
        }
    }


}
