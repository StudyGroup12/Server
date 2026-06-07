package com.studygroup.domain.todo.service;

import com.studygroup.domain.todo.dto.CompleteTodoRequest;
import com.studygroup.domain.todo.dto.CreateTodoRequest;
import com.studygroup.domain.todo.dto.TodoProgressResponse;
import com.studygroup.domain.todo.dto.TodoResponse;
import com.studygroup.domain.todo.dto.UpdateTodoRequest;
import com.studygroup.domain.todo.entity.PersonalTodo;
import com.studygroup.domain.todo.repository.PersonalTodoRepository;
import com.studygroup.global.exception.CustomException;
import com.studygroup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalTodoService {

    private final PersonalTodoRepository personalTodoRepository;

    @Transactional
    public TodoResponse createTodo(CreateTodoRequest request, Long memberId) {
        PersonalTodo todo = PersonalTodo.builder()
                .memberId(memberId)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .build();

        return TodoResponse.from(personalTodoRepository.save(todo));
    }

    public Page<TodoResponse> getTodos(Long memberId, Boolean completed, Pageable pageable) {
        Page<PersonalTodo> todos = completed == null
                ? personalTodoRepository.findByMemberIdOrderByCompletedAscDueDateAscCreatedAtDesc(memberId, pageable)
                : personalTodoRepository.findByMemberIdAndCompletedOrderByDueDateAscCreatedAtDesc(
                        memberId,
                        completed,
                        pageable
                );

        return todos.map(TodoResponse::from);
    }

    public TodoProgressResponse getProgress(Long memberId) {
        long totalCount = personalTodoRepository.countByMemberId(memberId);
        long completedCount = personalTodoRepository.countByMemberIdAndCompleted(memberId, true);
        return TodoProgressResponse.of(totalCount, completedCount);
    }

    public TodoResponse getTodo(Long todoId, Long memberId) {
        return TodoResponse.from(getOwnedTodo(todoId, memberId));
    }

    @Transactional
    public TodoResponse updateTodo(Long todoId, UpdateTodoRequest request, Long memberId) {
        PersonalTodo todo = getOwnedTodo(todoId, memberId);
        todo.update(request.getTitle(), request.getDescription(), request.getDueDate());
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse updateComplete(Long todoId, CompleteTodoRequest request, Long memberId) {
        PersonalTodo todo = getOwnedTodo(todoId, memberId);
        todo.updateCompleted(request.getCompleted());
        return TodoResponse.from(todo);
    }

    @Transactional
    public void deleteTodo(Long todoId, Long memberId) {
        personalTodoRepository.delete(getOwnedTodo(todoId, memberId));
    }

    private PersonalTodo getOwnedTodo(Long todoId, Long memberId) {
        PersonalTodo todo = personalTodoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
        if (!todo.isOwnedBy(memberId)) {
            throw new CustomException(ErrorCode.TODO_NOT_FOUND);
        }
        return todo;
    }
}
