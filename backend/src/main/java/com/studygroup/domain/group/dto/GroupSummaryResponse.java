package com.studygroup.domain.group.dto;

import com.studygroup.domain.group.entity.StudyGroup;
import java.time.LocalDateTime;

public record GroupSummaryResponse(
        Long id,
        String name,
        String description,
        String category,
        Integer maxMemberCount,
        Integer currentMemberCount,
        LocalDateTime createdAt
) {

    // Entity를 그룹 목록 화면에 필요한 응답 DTO로 변환한다.
    public static GroupSummaryResponse from(StudyGroup group) {
        return new GroupSummaryResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCategory(),
                group.getMaxMemberCount(),
                group.getCurrentMemberCount(),
                group.getCreatedAt()
        );
    }
}
