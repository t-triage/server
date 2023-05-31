/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.suite;

import com.clarolab.runner.category.CircleCIConnectorCategory;
import com.clarolab.runner.custom.BasePackage;
import com.clarolab.runner.custom.Categories;
import com.clarolab.runner.custom.CategorizedSuite;
import org.junit.runner.RunWith;

@Categories(categoryClasses = {CircleCIConnectorCategory.class})
@BasePackage(name = "com.clarolab")
@RunWith(CategorizedSuite.class)
public class CircleConnectorTestSuite {
}
