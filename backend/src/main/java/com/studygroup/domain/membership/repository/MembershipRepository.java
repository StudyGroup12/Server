package com.studygroup.domain.membership.repository;

import com.studygroup.domain.membership.entity.Membership;
import com.studygroup.domain.membership.entity.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByGroupId(Long groupId);
    List<Membership> findByGroupIdAndStatus(Long groupId, MembershipStatus status);
    Optional<Membership> findByGroupIdAndMemberId(Long groupId, Long memberId);
    boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);
}
