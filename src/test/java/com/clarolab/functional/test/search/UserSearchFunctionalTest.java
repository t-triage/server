/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.search;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.User;
import com.clarolab.populate.DataProvider;
import com.clarolab.service.UserService;
import com.clarolab.service.filter.FilterCriteria;
import com.clarolab.service.filter.FilterSpecification;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class UserSearchFunctionalTest extends BaseFunctionalTest {

    @Autowired
    UserService userService;

    @Test
    public void testEmptyUsernameResult() {
        FilterSpecification spec = new FilterSpecification(new FilterCriteria("username", ":", "noname"));

        List<User> results = userService.findAll(spec, Pageable.unpaged()).getContent();
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchOneUsernameResult() {
        User user = DataProvider.getUserAsAdmin();
        user = userService.save(user);

        FilterSpecification spec = new FilterSpecification(new FilterCriteria("username", ":", user.getUsername()));

        List<User> results = userService.findAll(spec, Pageable.unpaged()).getContent();
        Assert.assertFalse(results.isEmpty());
        Assert.assertTrue(results.contains(user));
    }

    @Test
    public void testSearchOneRealnameResult() {
        User user = DataProvider.getUserAsAdmin();
        user = userService.save(user);

        FilterSpecification spec = new FilterSpecification(new FilterCriteria("realname", ":", user.getRealname()));

        List<User> results = userService.findAll(spec, Pageable.unpaged()).getContent();
        Assert.assertFalse(results.isEmpty());
        Assert.assertTrue(results.contains(user));
    }

}
