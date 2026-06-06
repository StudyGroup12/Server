package com.studygroup.domain.membership.dto;

import com.studygroup.domain.auth.entity.Member;
import com.studygroup.domain.membership.entity.Membership;
import com.studygroup.domain.membership.entity.MembershipRole;
import lombok.Getter;

@Getter
public class MemberSummaryResponse {
    private final Long memberId;
    private final String email;
    private final String nickname;
    private final MembershipRole role;

    public MemberSummaryResponse(Member member, Membership membership) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.role = membership.getRole();
    }
}
