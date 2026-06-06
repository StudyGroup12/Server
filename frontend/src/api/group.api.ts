import api from './axios';
import { ApiResponse } from '../types/auth.types';
import {
  GroupDetail,
  GroupFormData,
  GroupSearchParams,
  GroupSummary,
  PageResponse,
} from '../types/group.types';

export const fetchGroups = async (
  params: GroupSearchParams = {}
): Promise<ApiResponse<PageResponse<GroupSummary>>> => {
  const response = await api.get('/api/groups', { params });
  return response.data;
};

export const fetchGroupDetail = async (groupId: number): Promise<ApiResponse<GroupDetail>> => {
  const response = await api.get(`/api/groups/${groupId}`);
  return response.data;
};

export const createGroup = async (data: GroupFormData): Promise<ApiResponse<GroupDetail>> => {
  const response = await api.post('/api/groups', data);
  return response.data;
};

export const updateGroup = async (
  groupId: number,
  data: GroupFormData
): Promise<ApiResponse<GroupDetail>> => {
  const response = await api.put(`/api/groups/${groupId}`, data);
  return response.data;
};

export const deleteGroup = async (groupId: number): Promise<ApiResponse<void>> => {
  const response = await api.delete(`/api/groups/${groupId}`);
  return response.data;
};
