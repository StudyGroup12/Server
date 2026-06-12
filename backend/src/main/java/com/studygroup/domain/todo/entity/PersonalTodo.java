package com.studygroup.domain.todo.entity;

import com.studygroup.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "personal_todos",
        indexes = {
                @Index(name = "idx_personal_todos_member", columnList = "memberId"),
                @Index(name = "idx_personal_todos_member_completed", columnList = "memberId,completed")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalTodo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDate dueDate;

    @Column(nullable = false)
    private boolean completed;

    private LocalDateTime completedAt;

    @Builder
    public PersonalTodo(Long memberId, String title, String description, LocalDate dueDate) {
        this.memberId = memberId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
        this.completedAt = null;
    }

    public void update(String title, String description, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public void updateCompleted(boolean completed) {
        this.completed = completed;
        this.completedAt = completed ? LocalDateTime.now() : null;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }
}
