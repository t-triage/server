/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.ci;

import com.clarolab.functional.BaseFunctionalTest;
import org.junit.Assert;
import org.junit.Test;

public class DuplicateClassSampleTest extends BaseFunctionalTest {

    @Test
    public void failDuplicated() {
        Assert.assertNull("It is expected to fail", "Hello");
    }

}
