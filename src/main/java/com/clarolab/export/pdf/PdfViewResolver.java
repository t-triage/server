/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.export.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

public class PdfViewResolver implements ViewResolver {

    @Autowired
    PdfView pdfView;

    @Override
    public View resolveViewName(String s, Locale locale) throws Exception {
        return pdfView;
    }
}