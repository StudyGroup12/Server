export type MembershipStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';
export type MembershipRole = 'OWNER' | 'MEMBER';

export interface MembershipResponse {
  id: number;
  memberId: number;
  groupId: number;
  status: MembershipStatus;
  role: MembershipRole;
}

export interface MemberSummary {
  memberId: number;
  email: string;
  nickname: string;
  role: MembershipRole;
}
