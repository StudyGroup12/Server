package com.studygroup.domain.group.service;

import com.studygroup.domain.group.dto.CreateGroupRequest;
import com.studygroup.domain.group.dto.GroupDetailResponse;
import com.studygroup.domain.group.dto.GroupSummaryResponse;
import com.studygroup.domain.group.dto.UpdateGroupRequest;
import com.studygroup.domain.group.entity.StudyGroup;
import com.studygroup.domain.group.repository.StudyGroupRepository;
import com.studygroup.global.exception.CustomException;
import com.studygroup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;

    public Page<GroupSummaryResponse> findGroups(String keyword, Pageable pageable) {
        Page<StudyGroup> groups = StringUtils.hasText(keyword)
                ? studyGroupRepository.searchByKeyword(keyword.trim(), pageable)
                : studyGroupRepository.findAll(pageable);

        return groups.map(GroupSummaryResponse::from);
    }

    public GroupDetailResponse findGroup(Long groupId) {
        return GroupDetailResponse.from(getGroup(groupId));
    }

    @Transactional
    public GroupDetailResponse createGroup(CreateGroupRequest request, Long ownerId) {
        StudyGroup group = StudyGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .maxMemberCount(request.getMaxMemberCount())
                .ownerId(ownerId)
                .build();

        return GroupDetailResponse.from(studyGroupRepository.save(group));
    }

    @Transactional
    public GroupDetailResponse updateGroup(Long groupId, UpdateGroupRequest request, Long memberId) {
        StudyGroup group = getGroup(groupId);
        validateOwner(group, memberId);
        validateMaxMemberCount(group, request.getMaxMemberCount());

        group.update(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getMaxMemberCount()
        );

        return GroupDetailResponse.from(group);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long memberId) {
        StudyGroup group = getGroup(groupId);
        validateOwner(group, memberId);
        studyGroupRepository.delete(group);
    }

    private StudyGroup getGroup(Long groupId) {
        return studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private void validateOwner(StudyGroup group, Long memberId) {
        if (!group.isOwnedBy(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateMaxMemberCount(StudyGroup group, Integer maxMemberCount) {
        if (maxMemberCount < group.getCurrentMemberCount()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }
}
