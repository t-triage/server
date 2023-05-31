/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit;

import com.clarolab.runner.category.UnitTestCategory;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTestCategory.class)
public abstract class BaseUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

}
