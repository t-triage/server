package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Notification;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.LicenseService;
import com.clarolab.service.NotificationService;
import com.clarolab.service.UserService;
import com.clarolab.startup.License;
import io.jsonwebtoken.lang.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void createNotificationTest() {
        List<Notification> notifications = notificationService.createNotification("", "", 0, userService.getAllAdminUser());

        Assert.notEmpty(notifications);
        Assert.isTrue(notifications.size() == userService.getAllAdminUser().size());

    }

    @Test
    public void getNotifications() {
        int max = 50;

        User user = provider.getUser();

        for (int x = 0; x < max * 2; ++x)
            notificationService.createNotification("", "", 0, user);

        List<Notification> notifications = notificationService.getNotifications(user);

        Assert.notEmpty(notifications);
        Assert.isTrue(notificationService.findAll().stream().filter(x -> x.getUser() == user).collect(Collectors.toList()).size() > max);
        Assert.isTrue(notifications.size() == max);
        Assert.isTrue(notificationService.getUnseenNotificationsCount(user) == max);

    }

}
