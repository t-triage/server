/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.filter.xss;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class XSSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        XSSRequestWrapper servletRequest = new XSSRequestWrapper((HttpServletRequest) request);
        chain.doFilter(servletRequest, response);
    }

}
