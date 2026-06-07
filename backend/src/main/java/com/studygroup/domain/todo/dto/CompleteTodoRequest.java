package com.studygroup.domain.todo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompleteTodoRequest {

    @NotNull(message = "완료 여부를 입력해주세요.")
    private Boolean completed;
}
