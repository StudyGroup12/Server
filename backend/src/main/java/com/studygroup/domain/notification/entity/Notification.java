package com.studygroup.domain.notification.entity;

import com.studygroup.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_recipient", columnList = "recipientId,readAt")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(nullable = false, length = 300)
    private String message;

    /** 클릭 시 이동할 상대 경로. null이면 이동 대상 없음(예: 삭제된 일정). */
    @Column(length = 300)
    private String link;

    /** null이면 안 읽음. */
    private LocalDateTime readAt;

    @Builder
    public Notification(Long recipientId, Long groupId, NotificationType type, String message, String link) {
        this.recipientId = recipientId;
        this.groupId = groupId;
        this.type = type;
        this.message = message;
        this.link = link;
    }

    public boolean isRead() {
        return this.readAt != null;
    }

    public boolean isRecipient(Long memberId) {
        return this.recipientId.equals(memberId);
    }

    public void markRead(LocalDateTime readAt) {
        if (this.readAt == null) {
            this.readAt = readAt;
        }
    }
}
