package com.clarolab.controller.impl;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.NotificationController;
import com.clarolab.dto.NotificationDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.NotificationServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class NotificationControllerImpl extends BaseControllerImpl<NotificationDTO> implements NotificationController {

    @Autowired
    private NotificationServiceDTO notificationServiceDTO;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Override
    protected TTriageService<NotificationDTO> getService() {
        return notificationServiceDTO;
    }

    @Override
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        return ResponseEntity.ok(notificationServiceDTO.getNotifications(authContextHelper.getCurrentUser()));
    }

    @Override
    public ResponseEntity<Integer> getUnseenNotificationsCount() {
        return ResponseEntity.ok(notificationServiceDTO.getUnseenNotificationsCount(authContextHelper.getCurrentUser()));
    }

    @Override
    public ResponseEntity<Integer> markNotificationsAsSeen() {
        return ResponseEntity.ok(notificationServiceDTO.markNotificationsAsSeen(authContextHelper.getCurrentUser()));
    }
}
