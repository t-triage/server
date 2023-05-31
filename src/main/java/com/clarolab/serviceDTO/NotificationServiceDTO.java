package com.clarolab.serviceDTO;

import com.clarolab.dto.NotificationDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.NotificationMapper;
import com.clarolab.model.Notification;
import com.clarolab.model.User;
import com.clarolab.service.NotificationService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationServiceDTO implements BaseServiceDTO<Notification, NotificationDTO, NotificationMapper> {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public TTriageService<Notification> getService() {
        return this.notificationService;
    }

    @Override
    public Mapper<Notification, NotificationDTO> getMapper() {
        return this.notificationMapper;
    }

    @Override
    public BaseServiceDTO<Notification, NotificationDTO, NotificationMapper> getServiceDTO() {
        return this;
    }

    public List<NotificationDTO> getNotifications(User user) {
        List<NotificationDTO> notifications = new ArrayList<>();
        notificationService.getNotifications(user).forEach(notification -> notifications.add(notificationMapper.convertToDTO(notification)));
        return notifications;
    }

    public Integer getUnseenNotificationsCount(User user) {
        return notificationService.getUnseenNotificationsCount(user);
    }

    public Integer markNotificationsAsSeen(User user) {
        return notificationService.markNotificationsAsSeen(user);
    }
}
