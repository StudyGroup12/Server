package com.studygroup.domain.studytime.dto;

import com.studygroup.domain.studytime.entity.StudySession;

import java.time.LocalDateTime;

/**
 * 그룹 내 본인의 학습 타이머 현황.
 * running=true면 startedAt이 현재 진행 중인 세션의 시작 시각이고,
 * elapsedSeconds는 서버 기준 경과 초(타임존 영향 없이 클라이언트가 그대로 이어서 카운트).
 */
public record StudyTimerStatusResponse(
        boolean running,
        LocalDateTime startedAt,
        long elapsedSeconds,
        long todayMinutes,
        long weekMinutes,
        long totalMinutes
) {
    public static StudyTimerStatusResponse of(
            StudySession running, long elapsedSeconds,
            long todayMinutes, long weekMinutes, long totalMinutes) {
        return new StudyTimerStatusResponse(
                running != null,
                running != null ? running.getStartedAt() : null,
                elapsedSeconds,
                todayMinutes,
                weekMinutes,
                totalMinutes
        );
    }
}
