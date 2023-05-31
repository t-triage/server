/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.runner.suite;

import com.clarolab.unit.bvt.BVTUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BVTUnitTest.class,
})
public class BVTSuite {
}
