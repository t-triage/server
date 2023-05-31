/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.util;

import com.clarolab.service.filter.FilterSpecificationsBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SearchSpecificationUtil {

    private static Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");

    private SearchSpecificationUtil() {}

    public static <T> Specification getSearchSpec(String[] criteria) {
        if (StringUtils.isEmpty(criteria)) return null;

        FilterSpecificationsBuilder<T> builder = new FilterSpecificationsBuilder<>();
        Arrays.asList(criteria).forEach(c -> {
                    Matcher matcher = pattern.matcher(c + ",");
                    while (matcher.find())
                        builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
                }
        );

        return builder.build();
    }

}
