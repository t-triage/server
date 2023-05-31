package com.clarolab.util.listener;

import com.google.common.collect.Sets;
import lombok.extern.java.Log;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.Set;

@Log
public class TestListener extends RunListener {

    Set<String> failures = Sets.newConcurrentHashSet();

    @Override
    public void testStarted(Description description)  {
        log.info("");
        log.info("******************************************************************************");
        log.info("Starting test : " + description.getMethodName());
        log.info("From class    : " + description.getClassName());
        log.info("******************************************************************************");
        log.info("");
    }

    @Override
    public void testFinished(Description description) {
        if(!failures.contains(description.getMethodName())){
            log.info("");
            log.info("******************************************************************************");
            log.info("This test has passed");
        }
        log.info("");
        log.info("******************************************************************************");
        log.info("Finished test: " + description.getMethodName());
        log.info("******************************************************************************");
        log.info("");
    }

    @Override
    public void testFailure(Failure failure) {
        failures.add(failure.getDescription().getMethodName());
        log.info("******************************************************************************");
        log.info("This test has failed");
        log.info("******************************************************************************");
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        failures.add(failure.getDescription().getMethodName());
        log.info("******************************************************************************");
        log.info("Failed: " + failure.getDescription().getMethodName());
        log.info("******************************************************************************");
    }

    public void testRunFinished(Result result) {
        int total = result.getRunCount();
        int failed = result.getFailureCount();
        int ignored = result.getIgnoreCount();
        int passed = total - (failed + ignored);
        log.info("******************************************************************************");
        log.info("******************************************************************************");
        log.info(String.format("Total: %d, Passed: %d, Failed: %d, Ignored: %d", total, passed, failed, ignored));
        log.info("******************************************************************************");
        log.info("******************************************************************************");
    }

}
