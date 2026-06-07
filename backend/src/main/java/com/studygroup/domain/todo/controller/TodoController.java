package com.studygroup.domain.todo.controller;

import com.studygroup.domain.todo.dto.CompleteTodoRequest;
import com.studygroup.domain.todo.dto.CreateTodoRequest;
import com.studygroup.domain.todo.dto.TodoProgressResponse;
import com.studygroup.domain.todo.dto.TodoResponse;
import com.studygroup.domain.todo.dto.UpdateTodoRequest;
import com.studygroup.domain.todo.service.TodoService;
import com.studygroup.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/{groupId}/todos")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TodoResponse>>> getTodos(
            @PathVariable Long groupId,
            @RequestParam(required = false) Boolean completed,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(todoService.getTodos(groupId, memberId, completed, pageable)));
    }

    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<TodoProgressResponse>> getProgress(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(todoService.getProgress(groupId, memberId)));
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodo(
            @PathVariable Long groupId,
            @PathVariable Long todoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(todoService.getTodo(groupId, todoId, memberId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
            @PathVariable Long groupId,
            @RequestBody @Valid CreateTodoRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(todoService.createTodo(groupId, request, memberId)));
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable Long groupId,
            @PathVariable Long todoId,
            @RequestBody @Valid UpdateTodoRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(todoService.updateTodo(groupId, todoId, request, memberId)));
    }

    @PatchMapping("/{todoId}/complete")
    public ResponseEntity<ApiResponse<TodoResponse>> updateComplete(
            @PathVariable Long groupId,
            @PathVariable Long todoId,
            @RequestBody @Valid CompleteTodoRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(todoService.updateComplete(groupId, todoId, request, memberId)));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(
            @PathVariable Long groupId,
            @PathVariable Long todoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        todoService.deleteTodo(groupId, todoId, memberId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    private Long getMemberId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername());
    }
}
