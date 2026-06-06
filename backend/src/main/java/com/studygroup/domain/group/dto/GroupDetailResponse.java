package com.studygroup.domain.group.dto;

import com.studygroup.domain.group.entity.StudyGroup;
import java.time.LocalDateTime;

public record GroupDetailResponse(
        Long id,
        String name,
        String description,
        String category,
        Integer maxMemberCount,
        Integer currentMemberCount,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // Entity를 그룹 상세 화면에 필요한 응답 DTO로 변환한다.
    public static GroupDetailResponse from(StudyGroup group) {
        return new GroupDetailResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCategory(),
                group.getMaxMemberCount(),
                group.getCurrentMemberCount(),
                group.getOwnerId(),
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }
}
