package com.studygroup.domain.group.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateGroupRequest {

    @NotBlank(message = "그룹 이름을 입력해주세요.")
    @Size(max = 100, message = "그룹 이름은 100자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "그룹 설명을 입력해주세요.")
    @Size(max = 1000, message = "그룹 설명은 1000자 이하로 입력해주세요.")
    private String description;

    @NotBlank(message = "카테고리를 입력해주세요.")
    @Size(max = 50, message = "카테고리는 50자 이하로 입력해주세요.")
    private String category;

    @NotNull(message = "최대 인원을 입력해주세요.")
    @Min(value = 2, message = "최대 인원은 2명 이상이어야 합니다.")
    @Max(value = 100, message = "최대 인원은 100명 이하로 입력해주세요.")
    private Integer maxMemberCount;
}
