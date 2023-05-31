/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.notifications;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.onboard.Guide;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.GuideService;
import com.clarolab.service.UserReactionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GuideFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private GuideService guideService;

    @Autowired
    private UserReactionService userReactionService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void persistence() {
        provider.getGuide();
        provider.getUserReaction();
        Assert.assertEquals(1, userReactionService.findAll().size());
    }

    @Test
    public void pages() {
        Guide first = provider.getGuide();
        provider.setGuide(null);
        Guide guide = provider.getGuide();
        guide.setPageUrl(first.getPageUrl());
        guideService.update(guide);

        List<Guide> guides = guideService.list(guide.getPageUrl());
        Assert.assertEquals(2, guides.size());
    }


    @Test
    public void noPendingEventsCompleted() {
        provider.getGuide();
        provider.getUserReaction();

        List<Guide> guides = guideService.list(provider.getGuide().getPageUrl(), provider.getUserReaction().getUser());

        Assert.assertEquals(0, guides.size());
    }

    @Test
    public void noPendingEventsEmpty() {
        List<Guide> guides = guideService.list(provider.getGuide().getPageUrl(), provider.getUserReaction().getUser());

        Assert.assertEquals(0, guides.size());
    }

    @Test
    public void pendingEvents() {
        provider.setName("Guide1");
        Guide first = provider.getGuide();
        provider.getUserReaction();
        provider.setGuide(null);
        provider.setName("Guide2");
        Guide guide = provider.getGuide();
        guide.setPageUrl(first.getPageUrl());
        guideService.update(guide);

        List<Guide> guides = guideService.list(provider.getGuide().getPageUrl(), provider.getUserReaction().getUser());

        Assert.assertEquals(1, guides.size());
    }



}
