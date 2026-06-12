package com.studygroup.domain.todo.dto;

import com.studygroup.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        Long groupId,
        Long memberId,
        String title,
        String description,
        LocalDate dueDate,
        boolean completed,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getGroupId(),
                todo.getMemberId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getDueDate(),
                todo.isCompleted(),
                todo.getCompletedAt(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
