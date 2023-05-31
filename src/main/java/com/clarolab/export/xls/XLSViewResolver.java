/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.xls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

public class XLSViewResolver implements ViewResolver {

    @Autowired
    XLSView XLSView;

    @Override
    public View resolveViewName(String s, Locale locale) throws Exception {
        return XLSView;
    }
}
