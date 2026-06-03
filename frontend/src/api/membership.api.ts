import axiosInstance from './axios';
import { ApiResponse } from '../types/auth.types';
import { MemberSummary, MembershipResponse } from '../types/membership.types';

export const applyMembership = async (groupId: number): Promise<MembershipResponse> => {
  const response = await axiosInstance.post<ApiResponse<MembershipResponse>>(`/api/groups/${groupId}/members/apply`);
  return response.data.data;
};

export const approveMembership = async (groupId: number, targetMemberId: number): Promise<void> => {
  await axiosInstance.post<ApiResponse<void>>(`/api/groups/${groupId}/members/${targetMemberId}/approve`);
};

export const rejectMembership = async (groupId: number, targetMemberId: number): Promise<void> => {
  await axiosInstance.post<ApiResponse<void>>(`/api/groups/${groupId}/members/${targetMemberId}/reject`);
};

export const fetchGroupMembers = async (groupId: number): Promise<MemberSummary[]> => {
  const response = await axiosInstance.get<ApiResponse<MemberSummary[]>>(`/api/groups/${groupId}/members`);
  return response.data.data;
};

export const fetchMyMembership = async (groupId: number): Promise<MembershipResponse | null> => {
  const response = await axiosInstance.get<ApiResponse<MembershipResponse>>(`/api/groups/${groupId}/members/me`);
  return response.data.data;
};

export const fetchPendingMembers = async (groupId: number): Promise<MemberSummary[]> => {
  const response = await axiosInstance.get<ApiResponse<MemberSummary[]>>(`/api/groups/${groupId}/members/pending`);
  return response.data.data;
};

export const leaveGroup = async (groupId: number): Promise<void> => {
  await axiosInstance.delete<ApiResponse<void>>(`/api/groups/${groupId}/members/leave`);
};
