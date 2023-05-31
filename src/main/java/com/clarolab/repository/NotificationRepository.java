package com.clarolab.repository;

import com.clarolab.model.Notification;
import com.clarolab.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends BaseRepository<Notification> {
    List<Notification> findTop50ByUserAndEnabledTrueAndTimestampGreaterThanOrderByTimestampDesc(User user, long timestamp);
    Notification findTopByUserAndDescriptionAndEnabledTrueOrderByTimestampDesc(User user, String description);

    Integer countByUserAndEnabledTrueAndSeenFalseAndTimestampGreaterThan(User user, long timestamp);

    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.seen = true WHERE n.seen = false AND n.user = ?1")
    Integer markNotificationsAsSeen(User user);
}
