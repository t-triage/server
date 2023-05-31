package com.clarolab.runner.custom;

import lombok.extern.java.Log;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

@Log
public class RetryRule implements TestRule {
    private int retryCount;

    public RetryRule (int retryCount) {
        this.retryCount = retryCount;
    }

    public RetryRule(){
        this(0);
    }

    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null;

                // implement retry logic here
                for (int i = 0; i < retryCount; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
                        caughtThrowable = t;
                        //  System.out.println(": run " + (i+1) + " failed");
                        log.info(description.getDisplayName() + ": run " + (i + 1) + " failed.");
                    }
                }
                if(retryCount != 0){
                    log.warning(description.getDisplayName() + ": giving up after " + retryCount + " failures.");
                    throw caughtThrowable;
                }
            }
        };
    }
}
