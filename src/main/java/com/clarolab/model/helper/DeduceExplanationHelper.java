/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper;

import com.clarolab.model.types.DeducedReasonType;
import com.clarolab.util.DateUtils;
import com.clarolab.view.GroupedStatView;
import com.google.common.collect.Maps;

import java.util.Map;

public final class DeduceExplanationHelper {

    private static Map<DeducedReasonType, String> values = Maps.newHashMap();

    static {
        values.put(DeducedReasonType.DEFAULT, "");
        values.put(DeducedReasonType.UNDEFINED, "Unable to process this test. Check it manually.");
        values.put(DeducedReasonType.SKIP_DEFAULT, "Skipped test");
        values.put(DeducedReasonType.CANCELLED_DEFAULT, "Cancelled");
        values.put(DeducedReasonType.FAIL_DEFAULT, "It has some recent failures.");
        values.put(DeducedReasonType.NEW_PASS_WAS_PERMANENT, "This used to fail for long time and now it passed. Check to close ticket or any related issue.");
        values.put(DeducedReasonType.NEW_PASS_WASNT_FLAKY, "After some intermittent failures, this came again to the pass row.");
        values.put(DeducedReasonType.NEW_FAIL_WAS_PASSING, "This was a solid passing test for long time and now it has failed.");
        values.put(DeducedReasonType.NEW_FAIL_WAS_NEW_FAIL_NOT_TRIAGED, "This was a solid passing test for long time and now it has failed.");
        values.put(DeducedReasonType.FAIL_IS_PERMANENT, "The test keep failing and it is triaged as automatic.");
        values.put(DeducedReasonType.SAME_ERROR_TRIAGED_BEFORE, "Autotriaged since it was triaged before with the same error.");
        values.put(DeducedReasonType.NEW_PASS_WAS_FAIL, "Was failing before and now is marked as fixed by the CIt tool.");

        // New version of the rules
        values.put(DeducedReasonType.Rule1,"Flaky test with constant similar failures.");
        values.put(DeducedReasonType.Rule2,"Same testing environment issue as before.");
        values.put(DeducedReasonType.Rule3,"Product was working and it has the same automation error as before.");
        values.put(DeducedReasonType.Rule4,"There is a known product issue.");
        values.put(DeducedReasonType.Rule5,"Fail and it needs a new triage.");
        values.put(DeducedReasonType.Rule6,"The automation error was recently analyzed and acknowledged.");
        values.put(DeducedReasonType.Rule7,"Not critical, it is failing for similar errors.");
        values.put(DeducedReasonType.Rule8,"Automated test is already filed to fix.");
        values.put(DeducedReasonType.Rule9,"There is a known product bug.");
        values.put(DeducedReasonType.Rule10,"Old failure with same error as before.");
        values.put(DeducedReasonType.Rule11,"Even there is a recognized product bug, the automation error has changed.");
        values.put(DeducedReasonType.Rule12,"Old failure");
        values.put(DeducedReasonType.Rule13,"New Fail not triaged previously.");
        values.put(DeducedReasonType.Rule14,"Was a triaged new fail.");
        values.put(DeducedReasonType.Rule15,"Application have a bug and used to pass, why is it now failing?");
        values.put(DeducedReasonType.Rule16,"Solid pass test.");
        values.put(DeducedReasonType.Rule17,"It was failing but now the test was set as Fixed.");
        values.put(DeducedReasonType.Rule18,"It had a permanent fail and now it is Fixed.");
        values.put(DeducedReasonType.Rule19,"Fixed test.");
        values.put(DeducedReasonType.Rule20,"Flaky test is now passing.");
        values.put(DeducedReasonType.Rule21,"Failed test is passing.");
        values.put(DeducedReasonType.Rule22,"Old failure is now passing.");
        values.put(DeducedReasonType.Rule23,"Nice pass.");
        values.put(DeducedReasonType.Rule24,"Known application bug with an skip test.");
        values.put(DeducedReasonType.Rule25,"Test changed from Fail to skip.");
        values.put(DeducedReasonType.Rule26,"Old failure is now being skipped.");
        values.put(DeducedReasonType.Rule27,"Failing test has aborted.");
        values.put(DeducedReasonType.Rule28,"Old failure has aborted.");
        values.put(DeducedReasonType.Rule29,"Not critical, but it was skipped.");
        values.put(DeducedReasonType.Rule30,"Test was never triaged.");
        values.put(DeducedReasonType.Rule31,"Pending first triage.");
        values.put(DeducedReasonType.Rule32,"The first triage needed.");
        values.put(DeducedReasonType.Rule33,"Skipped test needs first triage.");
        values.put(DeducedReasonType.Rule34,"First triage is needed.");
        values.put(DeducedReasonType.Rule35,"First triage needed.");
        values.put(DeducedReasonType.Rule36,"Dataprovider test without explicit parameter.");
        values.put(DeducedReasonType.Rule37,"Dataprovider test without explicit parameters always requires manual triage.");
        values.put(DeducedReasonType.Rule38,"Property RULE_ENGINE_ON is switching off the automatic triage.");
        values.put(DeducedReasonType.Rule39,"Property RULE_ENGINE_ON is switching off the automatic triage, therefore it always requires manual triage.");
        values.put(DeducedReasonType.Rule40,"Test Failing for the same reason as before.");
        values.put(DeducedReasonType.Rule41,"Failure that needs new analysis.");
        values.put(DeducedReasonType.Rule42,"Solid pass test has failed.");
        values.put(DeducedReasonType.Rule43,"Flaky test with automation issue filed.");
        values.put(DeducedReasonType.Rule44,"Test wont be fixed.");
        values.put(DeducedReasonType.Rule45,"Retry didn't pass, but there was a previous pass in the same product build.");
        values.put(DeducedReasonType.Rule46,"Test in different suite has a product bug.");
        values.put(DeducedReasonType.Rule47,"Test in different suite has a test bug.");
    }

    private DeduceExplanationHelper() { }

    public static String getDeducedReasonExplanation(DeducedReasonType deducedReasonType) {
        if (deducedReasonType == null) {
            return null;
        }

        return values.getOrDefault(deducedReasonType, "No specified detail.");

    }
    
    public static String getPlainDetail(GroupedStatView view) {
        StringBuffer answer = new StringBuffer();
        final String newLine = "\n";

        // '$executorName' build #$buildName
        answer.append(view.getExecutorName());
        answer.append("#");
        answer.append(view.getBuildName());
        answer.append(newLine);
        
        // Executed: $timestamp 11/10 15:21:12 - Deadline: $date 11/15
        if (view.getTimestamp() > 0) {
            answer.append("Executed: ");
            answer.append(DateUtils.covertToString(view.getTimestamp(), DateUtils.DATE_HOUR_SMALL));
            answer.append(" - ");
        }
        answer.append("Deadline: ");
        answer.append(DateUtils.covertToString(Long.parseLong(view.getDate()), DateUtils.DATE_SMALL));
        answer.append(newLine);
        
        // Total Tests: $total, ()$fails + $newFails) fail, $skip skip, ($pass + $nowPass) pass
        answer.append("Total Tests: ");
        answer.append(view.getTotal());
        answer.append(", ");
        answer.append(view.getFails() + view.getNewFails());
        answer.append(" fail, ");
        answer.append(view.getSkip());
        answer.append(" skip, ");
        answer.append(view.getPassed() + view.getNowPassing());
        answer.append(" pass.");
        answer.append(newLine);
        
         if (view.getToTriage() == 0) {
            // Triage: Done
             answer.append("Triage: Done");
             answer.append(newLine);
         } else {
             // Triage: $toTriage pending, ($triaged - $permanent) triaged, $permanent autotriaged
             try {
                 answer.append("Triage: ");
                 answer.append(view.getToTriage());
                 answer.append(" pending, ");
                 answer.append(view.getTriaged() - view.getPermanent());
                 answer.append(" triaged, ");
                 answer.append(view.getPermanent());
                 answer.append(" autotriaged.");
                 answer.append(newLine);
             } catch (Exception ex) {
                 System.out.println("Error converting to date");
             }

          }
         
        // Automated tests pending to fix: $filedAutomations
        if (!view.getFiledAutomations().isEmpty()) {
            answer.append(newLine);
            answer.append("*Filed Automation Failures:*");
            answer.append(newLine);
            for (String testFail : view.getFiledAutomations()) {
                answer.append("- ");
                answer.append(testFail);
                answer.append(newLine);
            }
            answer.append(newLine);
        }

        // Product bugs in this suite: $filedProductBugs
        if (!view.getFiledProductBugs().isEmpty()) {
            answer.append(newLine);
            answer.append("*Filed Product Failures:*");
            answer.append(newLine);
            for (String bug : view.getFiledProductBugs()) {
                answer.append("- ");
                answer.append(bug);
                answer.append(newLine);
            }
            answer.append(newLine);
        }

        return answer.toString();
    }
}
