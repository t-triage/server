/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.util.Constants.DEFAULT_GOOGLE_ANALYTICS_UA;
import static com.clarolab.util.Constants.GOOGLE_ANALYTICS_UA;

@Component
public class GoogleAnalyticsService {

    public class GACategory{
        public static final String POPULATE = "POPULATE";
        public static final String GENERAL = "GENERAL";

    }
    public class GAEvent {
        public static final String POPULATE_MANUAL_EVENT = "POPULATE_MANUAL_EVENT";
        public static final String POPULATE_PUSH_EVENT = "POPULATE_PUSH_EVENT";
        public static final String TRIAGE_EVENT = "TRIAGE_EVENT";

    }

    @Autowired
    private PropertyService propertyService;

    private GoogleAnalytics ga;

    private GoogleAnalytics getGa() {
        if (ga == null) {
            String ua = propertyService.valueOf(GOOGLE_ANALYTICS_UA, DEFAULT_GOOGLE_ANALYTICS_UA);
            ga = GoogleAnalytics.builder().withTrackingId(ua).build();
        }
        return ga;

    }

    public void sendEvent(String category, String value) {
        getGa().event().eventCategory(category).eventAction(value).sendAsync();
    }


}
