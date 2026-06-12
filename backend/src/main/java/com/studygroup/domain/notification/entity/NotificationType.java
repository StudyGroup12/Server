package com.studygroup.domain.notification.entity;

public enum NotificationType {
    // 일정
    SCHEDULE_CREATED,
    SCHEDULE_UPDATED,
    SCHEDULE_DELETED,
    SCHEDULE_REMINDER,
    // 멤버십
    MEMBERSHIP_REQUESTED,
    MEMBERSHIP_APPROVED,
    MEMBERSHIP_REJECTED,
    // 게시판
    COMMENT_CREATED
}
