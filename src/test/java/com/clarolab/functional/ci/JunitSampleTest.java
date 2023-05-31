/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.ci;

import com.clarolab.functional.BaseFunctionalTest;
import org.junit.Assert;
import org.junit.Test;

public class JunitSampleTest extends BaseFunctionalTest {

    @Test
    public void pass() {
        Assert.assertNotNull("The should pass", "Hello");
    }

    @Test
    public void fail() {
        Assert.assertNull("It is expected to fail", "Hello");
    }

    @Test
    public void failI18n() {
        Assert.assertNull("Testing i18n: míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com", "Hello");
    }

    @Test
    public void xss() {
        Assert.assertNull("<script>alert('p0wn3d');</script> ", "Hello");
    }

}
