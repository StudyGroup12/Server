package com.studygroup.domain.todo.dto;

public record TodoProgressResponse(
        long totalCount,
        long completedCount,
        int progressRate
) {
    public static TodoProgressResponse of(long totalCount, long completedCount) {
        int progressRate = totalCount == 0 ? 0 : (int) Math.round((completedCount * 100.0) / totalCount);
        return new TodoProgressResponse(totalCount, completedCount, progressRate);
    }
}
