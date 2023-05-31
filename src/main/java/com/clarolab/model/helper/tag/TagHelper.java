/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper.tag;

import com.clarolab.model.Build;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.ErrorType;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;

import java.util.Arrays;

public final class TagHelper {

    public static final String AUTO_TRIAGED = "AUTO-TRIAGED";
    public static final String AUTO_TRIAGED_SAME_ERROR = "AUTO-TRIAGED-SAME-ERROR";
    public static final String NEED_TRIAGE = "NEED-TRIAGE";

    public static final String FIRST_TRIAGE = "NO-PREVIOUS-TRIAGE";
    public static final String TRIAGE_UPDATED = "TRIAGE-UPDATED";
    public static final String TRIAGE_CANDIDATE = "TRIAGE-CANDIDATE";
    public static final String FLAKY_TRIAGE = "FLAKY";
    public static final String SOLID_TEST = "SOLID";

    private static final String BUILD_TRIAGE = "BUILD-%s";
    private static final String TEST_TRIAGE = "TEST-%s";
    private static final String ERROR_TRIAGE = "PREVIOUS-ERROR-%s";

    @Deprecated
    public static String add(String... value) {
        return Arrays.asList(value).toString();
    }

    public static String empty() {
        return "";
    }

    public static String fromBuild(Build build) {
        return String.format(BUILD_TRIAGE, build.getStatus().name());
    }

    public static String fromError(ErrorType errorType) {
        return String.format(ERROR_TRIAGE, errorType.name());
    }

    public static String fromTest(TestExecution testExecution) {
        return String.format(TEST_TRIAGE, testExecution.getStatus().name());
    }

    public static String addNewTag(String tags, String newTag) {
        // Dont add it in case parameter is nothing
        if (LogicalCondition.OR(newTag == null, newTag.isEmpty())) {
            return tags;
        }
        // Dont add it in case the tag is already in the tags
        if (tags.indexOf(newTag) > 0) {
            return tags;
        }
        // Lets add it
        if (tags.length() > 0) {
            // adding respecting the [format]
            return add(tags.substring(0, tags.length() - 1), newTag);
        } else {
            // this is the first tag
            return add(newTag);
        }
    }

    public static String getUITags(String tags) {
        String[] tagsToRemove = {NEED_TRIAGE, TRIAGE_CANDIDATE};

        String result = tags;
        for (String tag: tagsToRemove) {
            result = result.replace(tag, "");
        }

        return result;
    }

    public static Tag.Builder getBaseTag(TestTriage testTriage){
        String tags = testTriage.getTags();
        Tag.Builder builder = new Tag.Builder();
        return !StringUtils.isEmpty(tags) && tags.contains(FLAKY_TRIAGE) ? builder.with(FLAKY_TRIAGE) : builder;

    }

}
