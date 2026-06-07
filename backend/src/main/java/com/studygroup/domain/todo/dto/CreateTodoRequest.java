package com.studygroup.domain.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateTodoRequest {

    @NotBlank(message = "할일 제목을 입력해주세요.")
    @Size(max = 200, message = "할일 제목은 200자 이하로 입력해주세요.")
    private String title;

    @Size(max = 2000, message = "할일 설명은 2000자 이하로 입력해주세요.")
    private String description;

    private LocalDate dueDate;
}
