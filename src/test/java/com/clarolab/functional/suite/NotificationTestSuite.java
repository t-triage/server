/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.notifications.SlackNotificationFunctionalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SlackNotificationFunctionalTest.class
})
public class NotificationTestSuite {
}
