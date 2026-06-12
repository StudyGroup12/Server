package com.studygroup.domain.notification.dto;

import com.studygroup.domain.notification.entity.Notification;
import com.studygroup.domain.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long groupId,
        String groupName,
        String link,
        NotificationType type,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse of(Notification n, String groupName) {
        return new NotificationResponse(
                n.getId(),
                n.getGroupId(),
                groupName,
                n.getLink(),
                n.getType(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
