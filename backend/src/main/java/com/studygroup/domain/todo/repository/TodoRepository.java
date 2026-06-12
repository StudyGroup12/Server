package com.studygroup.domain.todo.repository;

import com.studygroup.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByGroupIdOrderByCompletedAscDueDateAscCreatedAtDesc(Long groupId, Pageable pageable);

    Page<Todo> findByGroupIdAndCompletedOrderByDueDateAscCreatedAtDesc(
            Long groupId,
            boolean completed,
            Pageable pageable
    );

    long countByGroupId(Long groupId);

    long countByGroupIdAndCompleted(Long groupId, boolean completed);
}
