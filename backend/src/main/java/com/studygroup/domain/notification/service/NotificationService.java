package com.studygroup.domain.notification.service;

import com.studygroup.domain.group.entity.StudyGroup;
import com.studygroup.domain.group.repository.StudyGroupRepository;
import com.studygroup.domain.membership.entity.Membership;
import com.studygroup.domain.membership.entity.MembershipStatus;
import com.studygroup.domain.membership.repository.MembershipRepository;
import com.studygroup.domain.notification.dto.NotificationResponse;
import com.studygroup.domain.notification.dto.UnreadCountResponse;
import com.studygroup.domain.notification.entity.Notification;
import com.studygroup.domain.notification.entity.NotificationType;
import com.studygroup.domain.notification.repository.NotificationRepository;
import com.studygroup.global.exception.CustomException;
import com.studygroup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 도메인 비종속 알림 발송/조회 서비스.
 * 다른 도메인 서비스(일정/멤버십/게시판)가 이벤트 발생 시 notify·notifyGroup을 호출한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MembershipRepository membershipRepository;
    private final StudyGroupRepository studyGroupRepository;

    /** 단일 수신자에게 알림 발송. */
    @Transactional
    public void notify(Long recipientId, Long groupId, NotificationType type, String message, String link) {
        notificationRepository.save(Notification.builder()
                .recipientId(recipientId)
                .groupId(groupId)
                .type(type)
                .message(message)
                .link(link)
                .build());
    }

    /**
     * 그룹의 ACCEPTED 멤버 전원에게 알림 발송.
     * actorId가 null이 아니면 본인은 제외(시스템 발송이면 null로 전원 발송).
     */
    @Transactional
    public void notifyGroup(Long groupId, NotificationType type, Long actorId, String message, String link) {
        List<Membership> accepted = membershipRepository.findByGroupIdAndStatus(groupId, MembershipStatus.ACCEPTED);

        List<Notification> toCreate = new ArrayList<>();
        for (Membership m : accepted) {
            if (actorId != null && m.getMemberId().equals(actorId)) {
                continue;
            }
            toCreate.add(Notification.builder()
                    .recipientId(m.getMemberId())
                    .groupId(groupId)
                    .type(type)
                    .message(message)
                    .link(link)
                    .build());
        }
        if (!toCreate.isEmpty()) {
            notificationRepository.saveAll(toCreate);
        }
    }

    public Page<NotificationResponse> getMyNotifications(Long memberId, Pageable pageable) {
        Page<Notification> page = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(memberId, pageable);
        Map<Long, String> groupNames = loadGroupNames(
                page.getContent().stream().map(Notification::getGroupId).distinct().toList()
        );
        return page.map(n -> NotificationResponse.of(
                n, groupNames.getOrDefault(n.getGroupId(), "(삭제된 그룹)")
        ));
    }

    public UnreadCountResponse getUnreadCount(Long memberId) {
        return new UnreadCountResponse(notificationRepository.countByRecipientIdAndReadAtIsNull(memberId));
    }

    @Transactional
    public NotificationResponse markRead(Long notificationId, Long memberId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!n.isRecipient(memberId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_FORBIDDEN);
        }
        n.markRead(LocalDateTime.now());
        String groupName = studyGroupRepository.findById(n.getGroupId())
                .map(StudyGroup::getName)
                .orElse("(삭제된 그룹)");
        return NotificationResponse.of(n, groupName);
    }

    @Transactional
    public int markAllRead(Long memberId) {
        return notificationRepository.markAllRead(memberId, LocalDateTime.now());
    }

    private Map<Long, String> loadGroupNames(List<Long> groupIds) {
        if (groupIds.isEmpty()) {
            return Map.of();
        }
        return studyGroupRepository.findAllById(groupIds).stream()
                .collect(Collectors.toMap(StudyGroup::getId, StudyGroup::getName, (a, b) -> a));
    }
}
