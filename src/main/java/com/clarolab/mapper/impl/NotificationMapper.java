package com.clarolab.mapper.impl;

import com.clarolab.dto.NotificationDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Notification;
import com.clarolab.service.NotificationService;
import com.clarolab.service.UserService;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class NotificationMapper implements Mapper<Notification, NotificationDTO> {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();

        setEntryFields(notification, notificationDTO);

        notificationDTO.setDescription(notification.getDescription());
        notificationDTO.setPriority(notification.getPriority());
        notificationDTO.setSeen(notification.getSeen());
        notificationDTO.setSubject(notification.getSubject());
        notificationDTO.setActualDate(DateUtils.covertToString(notification.getTimestamp(), DateUtils.DATE_FORMAT_CIRCLE_HH_mm_ss));
        notificationDTO.setTimeAgo(DateUtils.timeAgo(DateUtils.now(), notification.getTimestamp()));
        notificationDTO.setUser(notification.getUser().getId());

        return notificationDTO;
    }

    @Override
    public Notification convertToEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }

        Notification notification;

        if (dto.getId() == null || dto.getId() < 1) {
            notification = Notification.builder()
                    .id(null)
                    .description(dto.getDescription())
                    .priority(dto.getPriority())
                    .subject(dto.getSubject())
                    .seen(dto.isSeen())
                    .user(getNullableByID(dto.getUser(), id -> userService.find(id)))
                    .build();
        } else {
            notification = notificationService.find(dto.getId());
            notification.setSeen(dto.isSeen());
            notification.setPriority(dto.getPriority());
        }

        return notification;
    }
}
