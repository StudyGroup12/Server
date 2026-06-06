package com.studygroup.domain.membership.dto;

import com.studygroup.domain.membership.entity.Membership;
import com.studygroup.domain.membership.entity.MembershipRole;
import com.studygroup.domain.membership.entity.MembershipStatus;
import lombok.Getter;

@Getter
public class MembershipResponse {
    private final Long id;
    private final Long memberId;
    private final Long groupId;
    private final MembershipStatus status;
    private final MembershipRole role;

    public MembershipResponse(Membership membership) {
        this.id = membership.getId();
        this.memberId = membership.getMemberId();
        this.groupId = membership.getGroupId();
        this.status = membership.getStatus();
        this.role = membership.getRole();
    }
}
