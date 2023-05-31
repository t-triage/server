package com.clarolab.service;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.model.Notification;
import com.clarolab.model.User;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class NotificationService extends BaseService<Notification> {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private UserService userService;

    @Override
    protected BaseRepository<Notification> getRepository() {
        return this.notificationRepository;
    }

    private long expirationTime() {
        Calendar cal = Calendar.getInstance();
        // Two months back
        cal.add(Calendar.MONTH, -2);
        return cal.getTimeInMillis();
    }

    public List<Notification> getNotifications(User user) {
        return notificationRepository.findTop50ByUserAndEnabledTrueAndTimestampGreaterThanOrderByTimestampDesc(user, expirationTime());
    }

    public Integer getUnseenNotificationsCount(User user) {
        Integer count = notificationRepository.countByUserAndEnabledTrueAndSeenFalseAndTimestampGreaterThan(user, expirationTime());
        return count > 50 ? 50 : count;
    }

    public Integer markNotificationsAsSeen(User user) {
        return notificationRepository.markNotificationsAsSeen(user);
    }

    public Notification createNotification(String subject, String description, Integer priority, User user) {
        if (user == null)
            return null;

        Notification notification = Notification.builder()
                .subject(subject)
                .description(description)
                .priority(priority)
                .user(user)
                .seen(false)
                .enabled(true)
                .build();

        return save(notification);
    }

    public Notification createNotification(String subject, String description, Integer priority) {
        return createNotification(subject, description, priority, authContextHelper.getCurrentUser());
    }

    public List<Notification> createNotification(String subject, String description, Integer priority, List<User> users) {
        if (users == null || users.isEmpty())
            return null;

        List<Notification> notifications = new ArrayList<>();
        for (User user : users)
            notifications.add(createNotification(subject, description, priority, user));

        return notifications;
    }

    public List<Notification> createNotificationIfUnseen(String subject, String description, Integer priority, List<User> users, long time) {
        if (users == null || users.isEmpty())
            return null;

        List<Notification> notifications = new ArrayList<>();
        for (User user : users){
            Notification lastNotification = notificationRepository.findTopByUserAndDescriptionAndEnabledTrueOrderByTimestampDesc(user, description);
            if(lastNotification != null){
                if(lastNotification.getSeen() || lastNotification.getTimestamp() < time)
                    notifications.add(createNotification(subject, description, priority, user));
            }
            else
                notifications.add(createNotification(subject, description, priority, user));
        }
        return notifications;
    }
}
