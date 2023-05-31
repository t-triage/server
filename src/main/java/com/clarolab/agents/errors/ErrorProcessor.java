/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.errors;

import com.clarolab.model.helper.exparser.StackTrace;
import com.clarolab.model.helper.exparser.StackTraceParser;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public interface ErrorProcessor {


    int END_INDEX_TO_CONSIDERED_SIMILAR = 3000;

    StatusType errorProcessorType();

    default ErrorType process(StackTrace stackTraceLastTriage, StackTrace stackTraceTestExecution) {
        ErrorType errorType = ErrorType.NOT_EQUAL;

        if (stackTraceLastTriage == null || stackTraceTestExecution == null) {
            return errorType;
        }

        boolean isErrorEqual = false;
        boolean isMessageEqual = false;
        boolean isCausedByEqual = false;
        boolean isErrorVerySimilar = false;


        if (stackTraceLastTriage.getExceptionType() == null) {
            isErrorEqual = false;
        } else {
            String cleanStackTextLastExecution = cleanStackText(stackTraceLastTriage.getExceptionType());
            String cleanStackTextCurrentExecution = cleanStackText(stackTraceTestExecution.getExceptionType());
            isErrorEqual = cleanStackTextLastExecution.equalsIgnoreCase(cleanStackTextCurrentExecution);

            if (!isErrorEqual)
                isErrorVerySimilar = compareIfAreVerySimilar(cleanStackTextLastExecution, cleanStackTextCurrentExecution);
        }
        if (stackTraceLastTriage.getMessage() == null) {
            isMessageEqual = false;
        } else {
            isMessageEqual = cleanStackText(stackTraceLastTriage.getMessage())
                    .equalsIgnoreCase(cleanStackText(stackTraceTestExecution.getMessage()));
        }


        return getErrorLevel(isErrorEqual, isMessageEqual, isErrorVerySimilar);
    }

    default List<StackTrace> analyzeStackTrace(String stack) {
        return StackTraceParser.parseAll(stack);
    }

    default String cleanStackText(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }

        try {
            text = text.replaceAll("\\s", "");

            int indexOf = -1;
            do {
                indexOf = text.indexOf("@") ;
                if (indexOf >= 0) {
                    int endIndex = indexOf + 9;
                    text = text.length() >= endIndex ?
                            text.replace(text.substring(indexOf, endIndex), "")
                            : text.replace(text.substring(indexOf, text.length()), "");
                }
            } while (indexOf >= 0);

            text = text.replaceAll("[\\d]", "");
            text = text.replaceAll("\n", "");
            text = text.replaceAll("@", "");
            text = text.replaceAll(":", "");
            text = text.replaceAll("\t", "");


            Pattern regex = Pattern.compile("[$&+,;=?.#|'<>-_^*()%!{}¿]");
            text = text.replaceAll(regex.pattern(), "");

        } catch (Exception e) {
            //Nothing to do... I just return the same text just in case
            System.err.println(e.getMessage());
        }

        return text;
    }

    default ErrorType process(String errorDetailLastTriage, String stackTraceLastTriage, String errorDetailTestExecution, String stackTraceTestExecution) {
        ErrorType errorType = ErrorType.NOT_EQUAL;

        if (stackTraceLastTriage == null || stackTraceTestExecution == null) {
            return errorType;
        }

        boolean isErrorEqual = false;

        boolean isMessageEqual = false;
        boolean isCausedByEqual = false;

        boolean isErrorVerySimilar = false;

        String cleanStackTextLastExecution = cleanStackText(stackTraceLastTriage);
        String cleanStackTextCurrentExecution = cleanStackText(stackTraceTestExecution);

        isErrorEqual = cleanStackTextLastExecution.equalsIgnoreCase(cleanStackTextCurrentExecution);

        if(!isErrorEqual)
            isErrorVerySimilar = compareIfAreVerySimilar(cleanStackTextLastExecution, cleanStackTextCurrentExecution);

        isMessageEqual = cleanStackText(errorDetailLastTriage).equalsIgnoreCase(cleanStackText(errorDetailTestExecution));

        return getErrorLevel(isErrorEqual, isMessageEqual, isErrorVerySimilar);
    }

    default boolean compareIfAreVerySimilar(String cleanStackTextLastExecution, String cleanStackTextCurrentExecution) {
        int toEndLast = cleanStackTextLastExecution.length() > END_INDEX_TO_CONSIDERED_SIMILAR ? END_INDEX_TO_CONSIDERED_SIMILAR : cleanStackTextLastExecution.length();
        int toEndCurrent = cleanStackTextCurrentExecution.length() > END_INDEX_TO_CONSIDERED_SIMILAR ? END_INDEX_TO_CONSIDERED_SIMILAR : cleanStackTextCurrentExecution.length();

        return cleanStackTextCurrentExecution.substring(0, toEndCurrent).equalsIgnoreCase(cleanStackTextLastExecution.substring(0, toEndLast));
    }

    default ErrorType getErrorLevel(boolean isErrorEqual, boolean isMessageEqual, boolean isErrorVerySimilar) {
        ErrorType errorType = ErrorType.NOT_EQUAL;
        if (isErrorEqual && isMessageEqual)
            errorType = ErrorType.EQUAL;

        else if (isErrorEqual || isErrorVerySimilar)
            errorType = ErrorType.VERY_SIMILAR;

        else if (isMessageEqual)
            errorType = ErrorType.SIMILAR;

        return errorType;
    }
}
