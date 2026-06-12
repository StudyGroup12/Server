package com.studygroup.domain.todo.service;

import com.studygroup.domain.group.entity.StudyGroup;
import com.studygroup.domain.group.repository.StudyGroupRepository;
import com.studygroup.domain.membership.entity.Membership;
import com.studygroup.domain.membership.entity.MembershipStatus;
import com.studygroup.domain.membership.repository.MembershipRepository;
import com.studygroup.domain.todo.dto.CompleteTodoRequest;
import com.studygroup.domain.todo.dto.CreateTodoRequest;
import com.studygroup.domain.todo.dto.TodoProgressResponse;
import com.studygroup.domain.todo.dto.TodoResponse;
import com.studygroup.domain.todo.dto.UpdateTodoRequest;
import com.studygroup.domain.todo.entity.Todo;
import com.studygroup.domain.todo.repository.TodoRepository;
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
public class TodoService {

    private final TodoRepository todoRepository;
    private final MembershipRepository membershipRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Transactional
    public TodoResponse createTodo(Long groupId, CreateTodoRequest request, Long memberId) {
        validateMember(groupId, memberId);

        Todo todo = Todo.builder()
                .groupId(groupId)
                .memberId(memberId)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .build();

        return TodoResponse.from(todoRepository.save(todo));
    }

    public Page<TodoResponse> getTodos(Long groupId, Long memberId, Boolean completed, Pageable pageable) {
        validateMember(groupId, memberId);

        Page<Todo> todos = completed == null
                ? todoRepository.findByGroupIdOrderByCompletedAscDueDateAscCreatedAtDesc(groupId, pageable)
                : todoRepository.findByGroupIdAndCompletedOrderByDueDateAscCreatedAtDesc(groupId, completed, pageable);

        return todos.map(TodoResponse::from);
    }

    public TodoProgressResponse getProgress(Long groupId, Long memberId) {
        validateMember(groupId, memberId);

        long totalCount = todoRepository.countByGroupId(groupId);
        long completedCount = todoRepository.countByGroupIdAndCompleted(groupId, true);
        return TodoProgressResponse.of(totalCount, completedCount);
    }

    public TodoResponse getTodo(Long groupId, Long todoId, Long memberId) {
        validateMember(groupId, memberId);
        Todo todo = getTodoInGroup(groupId, todoId);
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse updateTodo(Long groupId, Long todoId, UpdateTodoRequest request, Long memberId) {
        validateMember(groupId, memberId);
        Todo todo = getTodoInGroup(groupId, todoId);
        ensureOwnerOrGroupOwner(todo, groupId, memberId);

        todo.update(request.getTitle(), request.getDescription(), request.getDueDate());

        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse updateComplete(Long groupId, Long todoId, CompleteTodoRequest request, Long memberId) {
        validateMember(groupId, memberId);
        Todo todo = getTodoInGroup(groupId, todoId);

        todo.updateCompleted(request.getCompleted());

        return TodoResponse.from(todo);
    }

    @Transactional
    public void deleteTodo(Long groupId, Long todoId, Long memberId) {
        validateMember(groupId, memberId);
        Todo todo = getTodoInGroup(groupId, todoId);
        ensureOwnerOrGroupOwner(todo, groupId, memberId);

        todoRepository.delete(todo);
    }

    private Todo getTodoInGroup(Long groupId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
        if (!todo.getGroupId().equals(groupId)) {
            throw new CustomException(ErrorCode.TODO_NOT_FOUND);
        }
        return todo;
    }

    private void ensureOwnerOrGroupOwner(Todo todo, Long groupId, Long memberId) {
        if (todo.isOwnedBy(memberId)) {
            return;
        }

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
        if (!group.isOwnedBy(memberId)) {
            throw new CustomException(ErrorCode.NOT_TODO_OWNER);
        }
    }

    private void validateMember(Long groupId, Long memberId) {
        Membership membership = membershipRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_MEMBER));
        if (membership.getStatus() != MembershipStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.NOT_MEMBER);
        }
    }
}
