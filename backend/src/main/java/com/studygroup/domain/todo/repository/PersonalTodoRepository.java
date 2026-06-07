package com.studygroup.domain.todo.repository;

import com.studygroup.domain.todo.entity.PersonalTodo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalTodoRepository extends JpaRepository<PersonalTodo, Long> {

    Page<PersonalTodo> findByMemberIdOrderByCompletedAscDueDateAscCreatedAtDesc(Long memberId, Pageable pageable);

    Page<PersonalTodo> findByMemberIdAndCompletedOrderByDueDateAscCreatedAtDesc(
            Long memberId,
            boolean completed,
            Pageable pageable
    );

    long countByMemberId(Long memberId);

    long countByMemberIdAndCompleted(Long memberId, boolean completed);
}
