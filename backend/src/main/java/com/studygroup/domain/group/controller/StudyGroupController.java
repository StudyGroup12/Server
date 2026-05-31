package com.studygroup.domain.group.controller;

import com.studygroup.domain.group.dto.CreateGroupRequest;
import com.studygroup.domain.group.dto.GroupDetailResponse;
import com.studygroup.domain.group.dto.GroupSummaryResponse;
import com.studygroup.domain.group.dto.UpdateGroupRequest;
import com.studygroup.domain.group.service.StudyGroupService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GroupSummaryResponse>>> findGroups(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(studyGroupService.findGroups(keyword, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupDetailResponse>> createGroup(
            @RequestBody @Valid CreateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(studyGroupService.createGroup(request, memberId)));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupDetailResponse>> findGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.success(studyGroupService.findGroup(groupId)));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupDetailResponse>> updateGroup(
            @PathVariable Long groupId,
            @RequestBody @Valid UpdateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(studyGroupService.updateGroup(groupId, request, memberId)));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        studyGroupService.deleteGroup(groupId, memberId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    private Long getMemberId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername());
    }
}
