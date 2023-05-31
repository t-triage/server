package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.NewsBoardDTO;
import com.clarolab.dto.NotificationDTO;
import com.clarolab.model.Notification;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.NotificationService;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class NotificationAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private NotificationService notificationService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void getNotificationsTest() {

        Notification notification = Notification.builder()
                .description("")
                .subject("")
                .seen(false)
                .user(provider.getUser())
                .priority(0)
                .build();

        notificationService.save(notification);

        TypeRef<List<NotificationDTO>> responseType = new TypeRef<List<NotificationDTO>>() {};

        List<NotificationDTO> notificationDTOList = given()
                .get(API_NOTIFICATION_URI + GET)
                .then()
                .extract().as(responseType.getType());

        Assert.assertNotNull(notificationDTOList);
        Assert.assertTrue(notificationDTOList.size() > 0);
    }
}
