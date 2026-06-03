package com.studygroup.domain.group.service;

import com.studygroup.domain.group.dto.CreateGroupRequest;
import com.studygroup.domain.group.dto.GroupDetailResponse;
import com.studygroup.domain.group.dto.GroupSummaryResponse;
import com.studygroup.domain.group.dto.UpdateGroupRequest;
import com.studygroup.domain.group.entity.StudyGroup;
import com.studygroup.domain.group.repository.StudyGroupRepository;
import com.studygroup.domain.membership.entity.Membership;
import com.studygroup.domain.membership.entity.MembershipRole;
import com.studygroup.domain.membership.entity.MembershipStatus;
import com.studygroup.domain.membership.repository.MembershipRepository;
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
    private final MembershipRepository membershipRepository;

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

        StudyGroup savedGroup = studyGroupRepository.save(group);

        Membership membership = Membership.builder()
                .groupId(savedGroup.getId())
                .memberId(ownerId)
                .status(MembershipStatus.ACCEPTED)
                .role(MembershipRole.OWNER)
                .build();
        membershipRepository.save(membership);

        return GroupDetailResponse.from(savedGroup);
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
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
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
